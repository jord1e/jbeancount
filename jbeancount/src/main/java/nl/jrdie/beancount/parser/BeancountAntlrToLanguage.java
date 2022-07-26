package nl.jrdie.beancount.parser;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static nl.jrdie.beancount.util.ImmutableKit.emptyList;
import static nl.jrdie.beancount.util.ImmutableKit.map;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import nl.jrdie.beancount.BeancountInvalidStateException;
import nl.jrdie.beancount.language.Account;
import nl.jrdie.beancount.language.AdditionExpression;
import nl.jrdie.beancount.language.Amount;
import nl.jrdie.beancount.language.ArithmeticExpression;
import nl.jrdie.beancount.language.BalanceDirective;
import nl.jrdie.beancount.language.BinaryCompoundExpression;
import nl.jrdie.beancount.language.BooleanValue;
import nl.jrdie.beancount.language.CloseDirective;
import nl.jrdie.beancount.language.Comment;
import nl.jrdie.beancount.language.Commodity;
import nl.jrdie.beancount.language.CommodityDirective;
import nl.jrdie.beancount.language.CompoundAmount;
import nl.jrdie.beancount.language.CompoundExpression;
import nl.jrdie.beancount.language.ConstantExpression;
import nl.jrdie.beancount.language.CostCompValue;
import nl.jrdie.beancount.language.CostSpec;
import nl.jrdie.beancount.language.CustomDirective;
import nl.jrdie.beancount.language.DateValue;
import nl.jrdie.beancount.language.DirectiveNode;
import nl.jrdie.beancount.language.DivisionExpression;
import nl.jrdie.beancount.language.DocumentDirective;
import nl.jrdie.beancount.language.Eol;
import nl.jrdie.beancount.language.EventDirective;
import nl.jrdie.beancount.language.Flag;
import nl.jrdie.beancount.language.IncludePragma;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.JournalDeclaration;
import nl.jrdie.beancount.language.LinkValue;
import nl.jrdie.beancount.language.Metadata;
import nl.jrdie.beancount.language.MetadataItem;
import nl.jrdie.beancount.language.MetadataKey;
import nl.jrdie.beancount.language.MetadataLine;
import nl.jrdie.beancount.language.MetadataValue;
import nl.jrdie.beancount.language.MultiplicationExpression;
import nl.jrdie.beancount.language.NegationExpression;
import nl.jrdie.beancount.language.NilValue;
import nl.jrdie.beancount.language.NoteDirective;
import nl.jrdie.beancount.language.OpenDirective;
import nl.jrdie.beancount.language.OptionPragma;
import nl.jrdie.beancount.language.PadDirective;
import nl.jrdie.beancount.language.ParenthesisedExpression;
import nl.jrdie.beancount.language.PluginPragma;
import nl.jrdie.beancount.language.PlusExpression;
import nl.jrdie.beancount.language.PopTagPragma;
import nl.jrdie.beancount.language.Posting;
import nl.jrdie.beancount.language.PragmaNode;
import nl.jrdie.beancount.language.PriceAnnotation;
import nl.jrdie.beancount.language.PriceDirective;
import nl.jrdie.beancount.language.PushTagPragma;
import nl.jrdie.beancount.language.QueryDirective;
import nl.jrdie.beancount.language.ScalarValue;
import nl.jrdie.beancount.language.SourceLocation;
import nl.jrdie.beancount.language.StringValue;
import nl.jrdie.beancount.language.SubtractionExpression;
import nl.jrdie.beancount.language.SymbolFlag;
import nl.jrdie.beancount.language.TagOrLink;
import nl.jrdie.beancount.language.TagValue;
import nl.jrdie.beancount.language.TransactionDirective;
import nl.jrdie.beancount.language.TxnFlag;
import nl.jrdie.beancount.language.UnaryCompoundExpression;
import nl.jrdie.beancount.parser.antlr.BeancountAntlrParser;
import nl.jrdie.beancount.util.Assert;
import nl.jrdie.beancount.util.ImmutableKit;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class BeancountAntlrToLanguage {

  private final String sourceName;

  public BeancountAntlrToLanguage(CommonTokenStream tokenStream, String sourceName) {
    this.sourceName = sourceName;
  }

  private SourceLocation getSourceLocation(Token token) {
    return AntlrHelper.createSourceLocation(token, sourceName);
  }

  public Journal createJournal(BeancountAntlrParser.JournalContext ctx) {
    Journal.Builder journal = Journal.newJournal();
    journal.sourceLocation(getSourceLocation(ctx.start));
    journal.declarations(createDeclarations(ctx.declarations()));
    return journal.build();
  }

  public List<JournalDeclaration<?, ?>> createDeclarations(
      BeancountAntlrParser.DeclarationsContext ctx) {
    if (ctx.decs != null && !ctx.decs.isEmpty()) {
      return map(ctx.decs, this::createDeclaration);
    }
    return ImmutableKit.emptyList();
  }

  public JournalDeclaration<?, ?> createDeclaration(BeancountAntlrParser.DeclarationContext ctx) {
    if (ctx.d != null) {
      return createDirective(ctx.d);
    } else if (ctx.p != null) {
      return createPragma(ctx.p);
    } else if (ctx.c != null) {
      return createComment(ctx.c);
    } else if (ctx.e != null) {
      return createEol(ctx.e);
    }
    Assert.shouldNeverHappen();
    return null;
  }

  public Eol createEol(Token token) {
    if (token.getType() != BeancountAntlrParser.EOL) {
      Assert.shouldNeverHappen();
      return null;
    }
    return Eol.newEol().sourceLocation(getSourceLocation(token)).build();
  }

  public DirectiveNode<?, ?> createDirective(BeancountAntlrParser.DirectiveContext ctx) {
    if (ctx.transaction() != null) {
      return createTransactionDirective(ctx.transaction());
    } else if (ctx.price() != null) {
      return createPriceDirective(ctx.price());
    } else if (ctx.balance() != null) {
      return createBalanceDirective(ctx.balance());
    } else if (ctx.open() != null) {
      return createOpenDirective(ctx.open());
    } else if (ctx.close() != null) {
      return createCloseDirective(ctx.close());
    } else if (ctx.commodity() != null) {
      return createCommodityDirective(ctx.commodity());
    } else if (ctx.pad() != null) {
      return createPadDirective(ctx.pad());
    } else if (ctx.document() != null) {
      return createDocumentDirective(ctx.document());
    } else if (ctx.note() != null) {
      return createNoteDirective(ctx.note());
    } else if (ctx.event() != null) {
      return createEventDirective(ctx.event());
    } else if (ctx.query() != null) {
      return createQueryDirective(ctx.query());
    } else if (ctx.custom() != null) {
      return createCustomDirective(ctx.custom());
    }
    return null;
  }

  public PragmaNode<?, ?> createPragma(BeancountAntlrParser.PragmaContext ctx) {
    if (ctx.pushtag() != null) {
      return createPushTagPragma(ctx.pushtag());
    } else if (ctx.poptag() != null) {
      return createPopTagPragma(ctx.poptag());
    } else if (ctx.option() != null) {
      return createOptionPragma(ctx.option());
    } else if (ctx.plugin() != null) {
      return createPluginPragma(ctx.plugin());
    } else if (ctx.include() != null) {
      return createIncludePragma(ctx.include());
    }
    return null;
  }

  public TransactionDirective createTransactionDirective(
      BeancountAntlrParser.TransactionContext ctx) {
    TransactionDirective.Builder txn = TransactionDirective.newTransactionDirective();
    BeancountAntlrParser.TransactionLineContext tl = ctx.tl;
    if (tl.pn != null) {
      BeancountAntlrParser.PayeeNarrationContext pn = tl.pn;
      if (pn.narration != null) {
        txn.narration(parseStringToken(pn.narration));
      }
      if (pn.payee != null) {
        txn.payee(parseStringToken(pn.payee));
      }
    }
    txn.postings(createPostingList(ctx.pl));
    txn.flag(createFlag(tl.flag));
    txn.date(parseDateToken(tl.date));
    txn.tagsAndLinks(createTagsAndLinks(tl.tagsAndLinks()));
    txn.sourceLocation(getSourceLocation(ctx.start));
    txn.metadata(createMetadata(ctx.m));
    if (tl.c != null) {
      txn.comment(createComment(tl.c));
    }
    return txn.build();
  }

  public List<Posting> createPostingList(BeancountAntlrParser.PostingListContext ctx) {
    if (ctx == null || ctx.pm == null) {
      return ImmutableKit.emptyList();
    }
    return map(ctx.pm, this::createPosting);
  }

  public Posting createPosting(BeancountAntlrParser.PostingWithMetadataContext ctx) {
    BeancountAntlrParser.PostingContext pctx = ctx.p;
    Posting.Builder posting = Posting.newPosting();
    posting.sourceLocation(getSourceLocation(ctx.start));
    posting.metadata(createMetadata(ctx.m));
    // This if-statement is only neccesairy because of the individual comment case.
    //  Maybe it should be revised later...
    if (pctx.a != null) {
      posting.account(createAccount(pctx.a));
      posting.flag(createFlag(pctx.flag));
    }
    if (pctx.e != null) {
      posting.amountExpression(createExpression(pctx.e));
    }
    if (pctx.c != null) {
      posting.commodity(createCommodity(pctx.c));
    }
    if (pctx.cs != null) {
      posting.costSpec(createCostSpec(pctx.cs));
    }
    if (pctx.comment != null) {
      posting.comment(createComment(pctx.comment));
    }
    if (pctx.pa != null) {
      boolean totalCost = pctx.atat != null;
      if (totalCost && pctx.at != null) {
        Assert.shouldNeverHappen();
      }
      posting.priceAnnotation(createPriceAnnotation(pctx.pa, totalCost));
    }
    return posting.build();
  }

  public PriceAnnotation createPriceAnnotation(
      BeancountAntlrParser.PriceAnnotationContext ctx, boolean totalCost) {
    PriceAnnotation.Builder priceAnnotation = PriceAnnotation.newPriceAnnotation();
    if (ctx.e != null) {
      priceAnnotation.priceExpression(createExpression(ctx.e));
    }
    if (ctx.c != null) {
      priceAnnotation.commodity(createCommodity(ctx.c));
    }
    priceAnnotation.totalCost(totalCost);
    return priceAnnotation.build();
  }

  public PriceDirective createPriceDirective(BeancountAntlrParser.PriceContext ctx) {
    PriceDirective.Builder price = PriceDirective.newPriceDirective();
    price.price(createAmount(ctx.a));
    price.commodity(createCommodity(ctx.c));
    price.date(parseDateToken(ctx.date));
    price.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    price.sourceLocation(getSourceLocation(ctx.start));
    price.metadata(createMetadata(ctx.m));
    if (ctx.comment != null) {
      price.comment(createComment(ctx.comment));
    }
    return price.build();
  }

  public BalanceDirective createBalanceDirective(BeancountAntlrParser.BalanceContext ctx) {
    BalanceDirective.Builder balance = BalanceDirective.newBalanceDirective();
    balance.amount(createAmountWithTolerance(ctx.amountWithTolerance()));
    balance.account(createAccount(ctx.account()));
    balance.date(parseDateToken(ctx.date));
    balance.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    balance.sourceLocation(getSourceLocation(ctx.start));
    balance.metadata(createMetadata(ctx.m));
    if (ctx.c != null) {
      balance.comment(createComment(ctx.c));
    }
    return balance.build();
  }

  public OpenDirective createOpenDirective(BeancountAntlrParser.OpenContext ctx) {
    OpenDirective.Builder open = OpenDirective.newOpenDirective();
    open.account(createAccount(ctx.a));
    open.commodities(createCommodityList(ctx.cl));
    if (ctx.bm != null) {
      open.bookingMethod(createBookingMethod(ctx.bm));
    }
    open.date(parseDateToken(ctx.date));
    open.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    open.sourceLocation(getSourceLocation(ctx.start));
    open.metadata(createMetadata(ctx.m));
    if (ctx.c != null) {
      open.comment(createComment(ctx.c));
    }
    return open.build();
  }

  public List<Commodity> createCommodityList(BeancountAntlrParser.CommodityListContext ctx) {
    if (ctx == null) {
      return emptyList();
    }
    return map(ctx.c, this::createCommodity);
  }

  public Flag createFlag(BeancountAntlrParser.TxnContext ctx) {
    if (ctx instanceof BeancountAntlrParser.TxnFlagContext) {
      return TxnFlag.newTxnFlag().build();
    }
    final String symbol = ctx.getText();
    final SymbolFlag.Type flagType = SymbolFlag.Type.ofSymbol(symbol);
    return SymbolFlag.newSymbolFlag().type(flagType).build();
  }

  public Flag createFlag(BeancountAntlrParser.OptFlagContext ctx) {
    if (ctx.getChildCount() == 0) {
      // Optimisation for the case where no flag is present.
      return null;
    }
    final String symbol = ctx.getText();
    final SymbolFlag.Type flagType = SymbolFlag.Type.ofSymbol(symbol);
    return SymbolFlag.newSymbolFlag().type(flagType).build();
  }

  public CloseDirective createCloseDirective(BeancountAntlrParser.CloseContext ctx) {
    CloseDirective.Builder close = CloseDirective.newCloseDirective();
    close.account(createAccount(ctx.a));
    close.date(parseDateToken(ctx.date));
    close.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    close.sourceLocation(getSourceLocation(ctx.start));
    close.metadata(createMetadata(ctx.m));
    if (ctx.c != null) {
      close.comment(createComment(ctx.c));
    }
    return close.build();
  }

  public CommodityDirective createCommodityDirective(BeancountAntlrParser.CommodityContext ctx) {
    CommodityDirective.Builder commodity = CommodityDirective.newCommodityDirective();
    commodity.commodity(createCommodity(ctx.c));
    commodity.date(parseDateToken(ctx.date));
    commodity.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    commodity.sourceLocation(getSourceLocation(ctx.start));
    commodity.metadata(createMetadata(ctx.m));
    if (ctx.comment != null) {
      commodity.comment(createComment(ctx.comment));
    }
    return commodity.build();
  }

  public PadDirective createPadDirective(BeancountAntlrParser.PadContext ctx) {
    PadDirective.Builder pad = PadDirective.newPadDirective();
    pad.sourceAccount(createAccount(ctx.sourceAccount));
    pad.targetAccount(createAccount(ctx.targetAccount));
    pad.date(parseDateToken(ctx.date));
    pad.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    pad.sourceLocation(getSourceLocation(ctx.start));
    pad.metadata(createMetadata(ctx.m));
    if (ctx.c != null) {
      pad.comment(createComment(ctx.c));
    }
    return pad.build();
  }

  public DocumentDirective createDocumentDirective(BeancountAntlrParser.DocumentContext ctx) {
    DocumentDirective.Builder document = DocumentDirective.newDocumentDirective();
    document.filename(parseStringToken(ctx.filename));
    document.account(createAccount(ctx.a));
    document.date(parseDateToken(ctx.date));
    document.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    document.sourceLocation(getSourceLocation(ctx.start));
    document.metadata(createMetadata(ctx.m));
    if (ctx.c != null) {
      document.comment(createComment(ctx.c));
    }
    return document.build();
  }

  public String createBookingMethod(BeancountAntlrParser.BookingMethodContext ctx) {
    return parseStringToken(ctx.s);
  }

  public NoteDirective createNoteDirective(BeancountAntlrParser.NoteContext ctx) {
    NoteDirective.Builder note = NoteDirective.newNoteDirective();
    note.note(parseStringToken(ctx.noteComment));
    note.account(createAccount(ctx.account()));
    note.date(parseDateToken(ctx.date));
    note.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    note.sourceLocation(getSourceLocation(ctx.start));
    note.metadata(createMetadata(ctx.m));
    if (ctx.c != null) {
      note.comment(createComment(ctx.c));
    }
    return note.build();
  }

  public EventDirective createEventDirective(BeancountAntlrParser.EventContext ctx) {
    EventDirective.Builder event = EventDirective.newEventDirective();
    event.description(parseStringToken(ctx.description));
    event.type(parseStringToken(ctx.type));
    event.date(parseDateToken(ctx.date));
    event.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    event.sourceLocation(getSourceLocation(ctx.start));
    event.metadata(createMetadata(ctx.m));
    if (ctx.c != null) {
      event.comment(createComment(ctx.c));
    }
    return event.build();
  }

  public QueryDirective createQueryDirective(BeancountAntlrParser.QueryContext ctx) {
    QueryDirective.Builder query = QueryDirective.newQueryDirective();
    query.sql(parseStringToken(ctx.sql));
    query.name(parseStringToken(ctx.name));
    query.date(parseDateToken(ctx.date));
    query.tagsAndLinks(createTagsAndLinks(ctx.tagsAndLinks()));
    query.sourceLocation(getSourceLocation(ctx.start));
    query.metadata(createMetadata(ctx.m));
    if (ctx.c != null) {
      query.comment(createComment(ctx.c));
    }
    return query.build();
  }

  public CustomDirective createCustomDirective(BeancountAntlrParser.CustomContext ctx) {
    CustomDirective.Builder custom = CustomDirective.newCustomDirective();
    custom.name(parseStringToken(ctx.name));
    custom.values(createScalarValueList(ctx.mvl));
    custom.date(parseDateToken(ctx.date));
    custom.sourceLocation(getSourceLocation(ctx.start));
    custom.metadata(createMetadata(ctx.m));
    if (ctx.c != null) {
      custom.comment(createComment(ctx.c));
    }
    return custom.build();
  }

  public List<ScalarValue> createScalarValueList(BeancountAntlrParser.CustomValueListContext ctx) {
    return map(ctx.v, this::createScalarValue);
  }

  public PushTagPragma createPushTagPragma(BeancountAntlrParser.PushtagContext ctx) {
    return null;
  }

  public PopTagPragma createPopTagPragma(BeancountAntlrParser.PoptagContext ctx) {
    return null;
  }

  public OptionPragma createOptionPragma(BeancountAntlrParser.OptionContext ctx) {
    OptionPragma.Builder option = OptionPragma.newOptionPragma();
    option.sourceLocation(getSourceLocation(ctx.start));
    option.name(parseStringToken(ctx.name));
    option.value(parseStringToken(ctx.value));
    if (ctx.c != null) {
      option.comment(createComment(ctx.c));
    }
    return option.build();
  }

  public IncludePragma createIncludePragma(BeancountAntlrParser.IncludeContext ctx) {
    IncludePragma.Builder include = IncludePragma.newIncludePragma();
    include.filename(parseStringToken(ctx.filename));
    include.sourceLocation(getSourceLocation(ctx.start));
    if (ctx.c != null) {
      include.comment(createComment(ctx.c));
    }
    return include.build();
  }

  public ScalarValue createScalarValue(BeancountAntlrParser.SimpleValueContext ctx) {
    if (ctx.s != null) {
      return createStringValue(ctx.s);
    } else if (ctx.c != null) {
      return createCommodity(ctx.c);
    } else if (ctx.a != null) {
      return createAccount(ctx.a);
    } else if (ctx.t != null) {
      return createTagValue(ctx.t);
    } else if (ctx.l != null) {
      return createLinkValue(ctx.l);
    } else if (ctx.d != null) {
      return createDateValue(ctx.d);
    } else if (ctx.b != null) {
      return createBooleanValue(ctx.b);
    } else if (ctx.n != null) {
      return createNilValue();
    } else if (ctx.e != null) {
      return createExpression(ctx.e);
    } else {
      Assert.shouldNeverHappen();
    }
    return null;
  }

  public Metadata createMetadata(BeancountAntlrParser.IndentedMetadataContext ctx) {
    return createMetadata(ctx.m);
  }

  public Metadata createMetadata(BeancountAntlrParser.MetadataContext ctx) {
    Metadata.Builder metadata = Metadata.newMetadata();
    if (ctx != null && ctx.ml != null) {
      metadata.metadata(map(ctx.ml, this::createMetadataLine));
    } else {
      metadata.metadata(emptyList());
    }
    return metadata.build();
  }

  public MetadataLine createMetadataLine(BeancountAntlrParser.MetadataLineContext ctx) {
    if (ctx.key != null) {
      MetadataItem.Builder mi = MetadataItem.newMetadataItem();
      mi.key(createMetadataKey(ctx.key));
      if (ctx.mv != null) {
        mi.value(createMetadataValue(ctx.mv));
      }
      return mi.build();
    } else if (ctx.l != null) {
      return createLinkValue(ctx.l);
    } else if (ctx.t != null) {
      return createTagValue(ctx.t);
    } else if (ctx.c != null) {
      return createComment(ctx.c);
    } else {
      Assert.shouldNeverHappen();
    }
    return null;
  }

  public MetadataValue createMetadataValue(BeancountAntlrParser.MetadataValueContext ctx) {
    if (ctx.sv != null) {
      return createScalarValue(ctx.sv);
    } else if (ctx.a != null) {
      return createAmount(ctx.a);
    } else {
      Assert.shouldNeverHappen();
    }
    return null;
  }

  public MetadataKey createMetadataKey(Token keyToken) {
    if (keyToken.getType() != BeancountAntlrParser.KEY) {
      Assert.shouldNeverHappen();
    }
    final String text = keyToken.getText();
    final String key = text.substring(0, text.length() - 1); // Strip trailing semicolon, i.e. key:
    return MetadataKey.newMetadataKey().key(key).build();
  }

  public NilValue createNilValue() {
    return NilValue.newNilValue().build();
  }

  public BooleanValue createBooleanValue(Token boolToken) {
    if (boolToken.getType() != BeancountAntlrParser.BOOL) {
      Assert.shouldNeverHappen();
    }
    boolean b =
        switch (boolToken.getText()) {
          case "TRUE" -> true;
          case "FALSE" -> false;
          default -> {
            Assert.shouldNeverHappen();
            yield false;
          }
        };
    return BooleanValue.newBooleanValue().value(b).build();
  }

  public PluginPragma createPluginPragma(BeancountAntlrParser.PluginContext ctx) {
    PluginPragma.Builder plugin = PluginPragma.newPluginPragma();
    plugin.name(parseStringToken(ctx.name));
    if (ctx.config != null) {
      plugin.config(parseStringToken(ctx.config));
    }
    plugin.sourceLocation(getSourceLocation(ctx.start));
    if (ctx.c != null) {
      plugin.comment(createComment(ctx.c));
    }
    return plugin.build();
  }

  public String parseStringToken(Token stringToken) {
    if (stringToken.getType() != BeancountAntlrParser.STRING) {
      Assert.shouldNeverHappen();
    }

    // Remove leading and trailing quote ("\"a\"" to "a")
    final String text = stringToken.getText();
    return text.substring(1, text.length() - 1);
  }

  private static final DateTimeFormatter ISO_LOCAL_DATE_WITH_SLASHES =
      new DateTimeFormatterBuilder()
          .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
          .appendLiteral('/')
          .appendValue(MONTH_OF_YEAR, 2)
          .appendLiteral('/')
          .appendValue(DAY_OF_MONTH, 2)
          .toFormatter(Locale.ROOT);

  public LocalDate parseDateToken(Token dateToken) {
    if (dateToken.getType() != BeancountAntlrParser.DATE) {
      Assert.shouldNeverHappen();
    }
    if (dateToken.getText().charAt(4) == '/') {
      // Second '/' (2015/12/23) should be implicit via ANTLR
      return LocalDate.parse(dateToken.getText(), ISO_LOCAL_DATE_WITH_SLASHES);
    }
    return LocalDate.parse(dateToken.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
  }

  public Account createAccount(BeancountAntlrParser.AccountContext ctx) {
    return Account.newAccount().account(ctx.getText()).build();
  }

  public Commodity createCommodity(Token commodityToken) {
    if (commodityToken.getType() != BeancountAntlrParser.CURRENCY) {
      Assert.shouldNeverHappen();
    }
    return Commodity.newCommodity().commodity(commodityToken.getText()).build();
  }

  public CostSpec createCostSpec(BeancountAntlrParser.CostSpecContext ctx) {
    CostSpec.Builder costSpec = CostSpec.newCostSpec();
    if (ctx instanceof BeancountAntlrParser.IndividualCostSpecContext cty) {
      costSpec.components(createCostCompList(cty.ccl));
      costSpec.doubleBraces(false);
    } else if (ctx instanceof BeancountAntlrParser.DoubleCostSpecContext cty) {
      costSpec.components(createCostCompList(cty.ccl));
      costSpec.doubleBraces(true);
    } else {
      Assert.shouldNeverHappen();
    }
    return costSpec.build();
  }

  public List<CostCompValue> createCostCompList(BeancountAntlrParser.CostCompListContext ctx) {
    if (ctx == null || ctx.cc == null) {
      return emptyList();
    }
    return map(ctx.cc, this::createCostComp);
  }

  public CostCompValue createCostComp(BeancountAntlrParser.CostCompContext ctx) {
    if (ctx.ca != null) {
      return createCompoundAmount(ctx.ca);
    } else if (ctx.date != null) {
      return createDateValue(ctx.date);
    } else if (ctx.s != null) {
      return createStringValue(ctx.s);
    } else {
      Assert.shouldNeverHappen();
    }
    return null;
  }

  public CompoundAmount createCompoundAmount(BeancountAntlrParser.CompoundAmountContext ctx) {
    CompoundAmount.Builder compoundAmount = CompoundAmount.newCompoundAmount();
    if (ctx.ce != null) {
      compoundAmount.compoundExpression(createCompoundExpression(ctx.ce));
      if (ctx.c != null) {
        compoundAmount.commodity(createCommodity(ctx.c));
      }
    } else {
      if (ctx.c == null) {
        Assert.shouldNeverHappen();
      } else {
        compoundAmount.commodity(createCommodity(ctx.c));
      }
    }
    return compoundAmount.build();
  }

  public CompoundExpression createCompoundExpression(BeancountAntlrParser.CompoundExprContext ctx) {
    if (ctx.e != null) {
      return UnaryCompoundExpression.newUnaryCompoundExpression()
          .expression(createExpression(ctx.e))
          .build();
    }
    BinaryCompoundExpression.Builder ce = BinaryCompoundExpression.newBinaryCompoundExpression();
    if (ctx.l != null) {
      ce.leftExpression(createExpression(ctx.l));
    }
    if (ctx.r != null) {
      ce.rightExpression(createExpression(ctx.r));
    }
    return ce.build();
  }

  public List<TagOrLink> createTagsAndLinks(BeancountAntlrParser.TagsAndLinksContext ctx) {
    if (ctx.getChildCount() == 0) {
      return ImmutableKit.emptyList();
    }
    List<TagOrLink> tagsAndLinks = new ArrayList<>();
    for (ParseTree child : ctx.children) {
      if (child instanceof TerminalNode node) {
        final Token token = node.getSymbol();
        if (token.getType() == BeancountAntlrParser.TAG) {
          tagsAndLinks.add(createTagValue(token));
        } else if (token.getType() == BeancountAntlrParser.LINK) {
          tagsAndLinks.add(createLinkValue(token));
        } else {
          Assert.shouldNeverHappen();
        }
      } else {
        Assert.shouldNeverHappen();
      }
    }
    return Collections.unmodifiableList(tagsAndLinks);
  }

  public DateValue createDateValue(Token dateToken) {
    LocalDate date = parseDateToken(dateToken);
    return DateValue.newDateValue().date(date).build();
  }

  public StringValue createStringValue(Token stringToken) {
    String value = parseStringToken(stringToken);
    return StringValue.newStringValue().value(value).build();
  }

  public TagValue createTagValue(Token tagToken) {
    if (tagToken.getType() != BeancountAntlrParser.TAG) {
      Assert.shouldNeverHappen();
    }
    final String tag = tagToken.getText().substring(1); // Strip leading #
    return TagValue.newTagValue().tag(tag).build();
  }

  public LinkValue createLinkValue(Token linkToken) {
    if (linkToken.getType() != BeancountAntlrParser.LINK) {
      Assert.shouldNeverHappen();
    }
    final String link = linkToken.getText().substring(1); // Strip leading ^
    return LinkValue.newLinkValue().link(link).build();
  }

  public Amount createAmount(BeancountAntlrParser.AmountContext ctx) {
    Amount.Builder amount = Amount.newAmount();
    amount.commodity(createCommodity(ctx.c));
    amount.expression(createExpression(ctx.e));
    amount.tolerance(null);
    return amount.build();
  }

  public Amount createAmountWithTolerance(BeancountAntlrParser.AmountWithToleranceContext ctx) {
    if (ctx.a != null) {
      return createAmount(ctx.a);
    }
    Amount.Builder amount = Amount.newAmount();
    amount.commodity(createCommodity(ctx.c));
    amount.expression(createExpression(ctx.e));
    amount.tolerance(parseNumberToken(ctx.tolerance));
    return amount.build();
  }

  public ArithmeticExpression createExpression(BeancountAntlrParser.ExprContext ctx) {
    if (ctx instanceof BeancountAntlrParser.ConstantContext expr) {
      return createConstantExpression(expr);
    } else if (ctx instanceof BeancountAntlrParser.NegationContext expr) {
      return createNegationExpression(expr);
    } else if (ctx instanceof BeancountAntlrParser.AdditionContext expr) {
      return createAdditionExpression(expr);
    } else if (ctx instanceof BeancountAntlrParser.SubtractionContext expr) {
      return createSubtractionExpression(expr);
    } else if (ctx instanceof BeancountAntlrParser.MultiplicationContext expr) {
      return createMultiplicationExpression(expr);
    } else if (ctx instanceof BeancountAntlrParser.DivisionContext expr) {
      return createDivisionExpression(expr);
    } else if (ctx instanceof BeancountAntlrParser.PlusContext expr) {
      return createPlusExpression(expr);
    } else if (ctx instanceof BeancountAntlrParser.ParenthesisedContext expr) {
      return createParenthesisedExpression(expr);
    }
    Assert.shouldNeverHappen();
    return null;
  }

  public BigDecimal parseNumberToken(Token numberToken) {
    if (numberToken.getType() != BeancountAntlrParser.NUMBER) {
      Assert.shouldNeverHappen();
    }
    return new BigDecimal(numberToken.getText());
  }

  public ConstantExpression createConstantExpression(BeancountAntlrParser.ConstantContext ctx) {
    ConstantExpression.Builder constant = ConstantExpression.newConstantExpression();
    constant.value(parseNumberToken(ctx.c));
    return constant.build();
  }

  public AdditionExpression createAdditionExpression(BeancountAntlrParser.AdditionContext ctx) {
    AdditionExpression.Builder addition = AdditionExpression.newAdditionExpression();
    addition.leftExpression(createExpression(ctx.l));
    addition.rightExpression(createExpression(ctx.r));
    return addition.build();
  }

  public SubtractionExpression createSubtractionExpression(
      BeancountAntlrParser.SubtractionContext ctx) {
    SubtractionExpression.Builder subtraction = SubtractionExpression.newSubtractionExpression();
    subtraction.leftExpression(createExpression(ctx.l));
    subtraction.rightExpression(createExpression(ctx.r));
    return subtraction.build();
  }

  public MultiplicationExpression createMultiplicationExpression(
      BeancountAntlrParser.MultiplicationContext ctx) {
    MultiplicationExpression.Builder multiplication =
        MultiplicationExpression.newMultiplicationExpression();
    multiplication.leftExpression(createExpression(ctx.l));
    multiplication.rightExpression(createExpression(ctx.r));
    return multiplication.build();
  }

  public DivisionExpression createDivisionExpression(BeancountAntlrParser.DivisionContext ctx) {
    DivisionExpression.Builder division = DivisionExpression.newDivisionExpression();
    division.leftExpression(createExpression(ctx.l));
    division.rightExpression(createExpression(ctx.r));
    return division.build();
  }

  public NegationExpression createNegationExpression(BeancountAntlrParser.NegationContext ctx) {
    NegationExpression.Builder negation = NegationExpression.newNegationExpression();
    negation.expression(createExpression(ctx.e));
    return negation.build();
  }

  public PlusExpression createPlusExpression(BeancountAntlrParser.PlusContext ctx) {
    PlusExpression.Builder plus = PlusExpression.newPlusExpression();
    plus.expression(createExpression(ctx.e));
    return plus.build();
  }

  public ParenthesisedExpression createParenthesisedExpression(
      BeancountAntlrParser.ParenthesisedContext ctx) {
    ParenthesisedExpression.Builder parenthesised =
        ParenthesisedExpression.newParenthesisedExpression();
    parenthesised.expression(createExpression(ctx.e));
    return parenthesised.build();
  }

  public Comment createComment(Token commentToken) {
    if (commentToken.getType() != BeancountAntlrParser.COMMENT) {
      throw new BeancountInvalidStateException();
    }
    Comment.Builder comment = Comment.newComment();
    // Remove leading ';', and keep possible ws after the semicolon.
    final String text = commentToken.getText().substring(1);
    comment.sourceLocation(getSourceLocation(commentToken));
    comment.comment(text);
    return comment.build();
  }
}
