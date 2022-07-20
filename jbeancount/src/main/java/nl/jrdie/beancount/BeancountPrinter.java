package nl.jrdie.beancount;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.Account;
import nl.jrdie.beancount.language.AdditionExpression;
import nl.jrdie.beancount.language.Amount;
import nl.jrdie.beancount.language.ArithmeticExpression;
import nl.jrdie.beancount.language.BalanceDirective;
import nl.jrdie.beancount.language.BinaryCompoundExpression;
import nl.jrdie.beancount.language.BooleanValue;
import nl.jrdie.beancount.language.CloseDirective;
import nl.jrdie.beancount.language.Commodity;
import nl.jrdie.beancount.language.CommodityDirective;
import nl.jrdie.beancount.language.CompoundAmount;
import nl.jrdie.beancount.language.CompoundExpression;
import nl.jrdie.beancount.language.ConstantExpression;
import nl.jrdie.beancount.language.CostSpec;
import nl.jrdie.beancount.language.CustomDirective;
import nl.jrdie.beancount.language.DateValue;
import nl.jrdie.beancount.language.DirectiveNode;
import nl.jrdie.beancount.language.DivisionExpression;
import nl.jrdie.beancount.language.EventDirective;
import nl.jrdie.beancount.language.Flag;
import nl.jrdie.beancount.language.IncludePragma;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.JournalDeclaration;
import nl.jrdie.beancount.language.Link;
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
import nl.jrdie.beancount.language.PriceAnnotation;
import nl.jrdie.beancount.language.PriceDirective;
import nl.jrdie.beancount.language.PushTagPragma;
import nl.jrdie.beancount.language.QueryDirective;
import nl.jrdie.beancount.language.StringValue;
import nl.jrdie.beancount.language.SubtractionExpression;
import nl.jrdie.beancount.language.Tag;
import nl.jrdie.beancount.language.TagOrLink;
import nl.jrdie.beancount.language.TagValue;
import nl.jrdie.beancount.language.TransactionDirective;
import nl.jrdie.beancount.language.UnaryCompoundExpression;
import nl.jrdie.beancount.util.Assert;

public class BeancountPrinter {

  private BeancountPrinter() {}

  public static BeancountPrinter newPrinter() {
    return new BeancountPrinter();
  }

  public int currencyColumn = 61; // Fava default
  public boolean compactMode = false;

  private int nestingLevel = 0;
  private int dentSize = 2;
  private int currentDent;
  private String indent;

  // Two levels of indentation is often the maximum (metadata on postings)
  private String[] indents = new String[2];

  public void indent() {
    nestingLevel++;
    maybeResizeIndents();
    updateIndent();
  }

  public void dedent() {
    nestingLevel--;
    updateIndent();
  }

  private void maybeResizeIndents() {
    if (indents.length <= nestingLevel) {
      indents = Arrays.copyOf(indents, indents.length + 1);
    }
  }

  private void updateIndent() {
    currentDent = nestingLevel * dentSize;
    this.indent =
        Objects.requireNonNullElseGet(
            indents[nestingLevel], () -> indents[nestingLevel] = " ".repeat(currentDent));
  }

  public void dent(PrintWriter pw) {
    pw.print(indent);
  }

  public void nl(PrintWriter pw) {
    pw.print('\n');
  }

  public void space(PrintWriter pw) {
    pw.print(' ');
  }

  public void quote(PrintWriter pw) {
    pw.print('"');
  }

  public String print(Journal journal) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    for (JournalDeclaration<?, ?> declaration : journal.declarations()) {
      if (declaration instanceof BalanceDirective bd) {
        print(pw, bd);
      } else if (declaration instanceof PadDirective pd) {
        print(pw, pd);
      } else if (declaration instanceof TransactionDirective td) {
        print(pw, td);
      } else if (declaration instanceof OpenDirective od) {
        print(pw, od);
      } else if (declaration instanceof CloseDirective cd) {
        print(pw, cd);
      } else if (declaration instanceof EventDirective ed) {
        print(pw, ed);
      } else if (declaration instanceof PriceDirective pd) {
        print(pw, pd);
      } else if (declaration instanceof NoteDirective nd) {
        print(pw, nd);
      } else if (declaration instanceof CommodityDirective cd) {
        print(pw, cd);
      } else if (declaration instanceof QueryDirective qd) {
        print(pw, qd);
      } else if (declaration instanceof CustomDirective cd) {
        print(pw, cd);
      } else if (declaration instanceof IncludePragma ip) {
        print(pw, ip);
      } else if (declaration instanceof OptionPragma op) {
        print(pw, op);
      } else if (declaration instanceof PluginPragma pp) {
        print(pw, pp);
      } else if (declaration instanceof PushTagPragma ptp) {
        print(pw, ptp);
      } else if (declaration instanceof PopTagPragma ptp) {
        print(pw, ptp);
      } else {
        Assert.shouldNeverHappen();
        return null;
      }
    }

    return sw.toString();
  }

  private void print(PrintWriter pw, IncludePragma ip) {
    pw.write("include \"");
    pw.write(ip.filename());
    pw.write('"');
    nl(pw);
  }

  private void print(PrintWriter pw, OptionPragma op) {
    pw.write("option \"");
    pw.write(op.name());
    pw.write("\" \"");
    pw.write(op.value());
    pw.write('"');
    nl(pw);
  }

  private void print(PrintWriter pw, PluginPragma pp) {
    pw.write("plugin \"");
    pw.write(pp.name());
    if (pp.config() != null) {
      pw.write("\" \"");
      pw.write(pp.config());
    }
    pw.write('"');
    nl(pw);
  }

  private void print(PrintWriter pw, PopTagPragma ptp) {
    throw new UnsupportedOperationException(
        "The poptag pragma is not yet supported by the printer");
  }

  private void print(PrintWriter pw, PushTagPragma ptp) {
    throw new UnsupportedOperationException(
        "The pushtag pragma is not yet supported by the printer");
  }

  private void print(PrintWriter pw, CustomDirective cd) {
    print(pw, cd.date());
    space(pw);
    pw.print("custom \"");
    pw.print(cd.name());
    pw.print('"');
    loop(cd.values(), () -> space(pw), v -> print(pw, v), () -> space(pw));
    // Custom handling because tags and links can be values
    nl(pw);
    print(pw, cd.metadata(), this::nl);
  }

  private void print(PrintWriter pw, QueryDirective qd) {
    pw.format("%s query \"%s\" \"%s\"", date(qd.date()), qd.name(), qd.sql());
    printDirective(pw, qd);
  }

  private void print(PrintWriter pw, CommodityDirective cd) {
    print(pw, cd.date());
    pw.write(" commodity ");
    pw.write(cd.commodity().commodity());
    printDirective(pw, cd);
  }

  private void print(PrintWriter pw, NoteDirective nd) {
    print(pw, nd.date());
    pw.write(" note ");
    pw.write(nd.account().account());
    quote(pw);
    pw.write(nd.comment());
    quote(pw);
    printDirective(pw, nd);
  }

  public String date(LocalDate date) {
    return DateTimeFormatter.ISO_DATE.format(date);
  }

  public String account(Account account) {
    return account.account();
  }

  public String commodity(Commodity commodity) {
    return commodity.commodity();
  }

  private void printDirective(PrintWriter pw, DirectiveNode<?, ?> node) {
    print(pw, this::space, node.tagsAndLinks());
    nl(pw);
    print(pw, node.metadata(), this::nl);
  }

  public void print(PrintWriter pw, BalanceDirective bd) {
    final String account = account(bd.account());
    final String amount = arithmeticExpression(bd.amount().expression());
    final int col =
        currencyColumn
            - 10
            - 9 /* " balance " */
            - account.length()
            - 1 /* " " */
            - 2 /* start at 1, and space before commodity */;
    final String format = "%s balance %s %" + Math.max(col, 1) + "s %s";
    pw.format(format, date(bd.date()), account, amount, commodity(bd.amount().commodity()));
    printDirective(pw, bd);
  }

  public void print(PrintWriter pw, PadDirective pd) {
    final String sourceAccount = account(pd.sourceAccount());
    final String targetAccount = account(pd.targetAccount());
    pw.format("%s pad %s %s", date(pd.date()), sourceAccount, targetAccount);
    printDirective(pw, pd);
  }

  public void print(PrintWriter pw, EventDirective ed) {
    pw.format("%s event \"%s\" \"%s\"", date(ed.date()), ed.type(), ed.description());
    printDirective(pw, ed);
  }

  private void print(PrintWriter pw, LocalDate ld) {
    pw.print(date(ld));
  }

  public void print(PrintWriter pw, OpenDirective od) {
    print(pw, od.date());
    pw.print(" open ");
    print(pw, od.account());
    loop(od.commodities(), () -> space(pw), c -> print(pw, c), () -> pw.print(','));
    if (od.bookingMethod() != null) {
      space(pw);
      pw.write(od.bookingMethod());
    }
    printDirective(pw, od);
  }

  public void print(PrintWriter pw, CloseDirective cd) {
    print(pw, cd.date());
    pw.print(" close ");
    print(pw, cd.account());
    printDirective(pw, cd);
  }

  public void print(PrintWriter pw, PriceDirective pd) {
    final String commodity = commodity(pd.commodity());
    final String price = arithmeticExpression(pd.price().expression());
    final String otherCommodity = commodity(pd.price().commodity());
    final int col =
        currencyColumn
            - 10
            - 7 /* " price " */
            - commodity.length()
            - 1 /* " " */
            - 2 /* start at 1, and space before commodity */;
    final String format = "%s price %s %" + Math.max(col, 1) + "s %s";
    pw.format(format, date(pd.date()), commodity, price, otherCommodity);
    printDirective(pw, pd);
  }

  public void print(PrintWriter pw, TransactionDirective td) {
    pw.print(date(td.date()));
    space(pw);
    print(pw, td.flag());
    if (td.payee() != null) {
      space(pw);
      quote(pw);
      pw.print(td.payee());
      quote(pw);
    }
    if (td.narration() != null) {
      space(pw);
      quote(pw);
      pw.print(td.narration());
      quote(pw);
    }
    printDirective(pw, td);
    final List<Posting> postings = td.postings();
    if (postings.isEmpty()) {
      return;
    }
    indent();
    dent(pw);
    loop(
        postings,
        p -> print(pw, p),
        () -> {
          nl(pw);
          dent(pw);
        });
    dedent();
    nl(pw);
    if (!compactMode) {
      nl(pw);
    }
  }

  public <T> void loop(Collection<T> collection, Consumer<T> action, Runnable it) {
    loop(collection, null, action, it);
  }

  public <T> void loop(
      Collection<T> collection, Runnable beforeIfExists, Consumer<T> action, Runnable it) {
    if (collection == null || collection.isEmpty()) {
      return;
    }
    if (beforeIfExists != null) {
      beforeIfExists.run();
    }
    int size = collection.size();
    int i = 0;
    for (T t : collection) {
      action.accept(t);
      if (++i < size) {
        it.run();
      }
    }
  }

  public void print(PrintWriter pw, Posting p) {
    if (p == null) {
      return; // TODO, this is a comment
    }
    final String account = account(p.account());
    final ArithmeticExpression ae = p.amountExpression();
    final String num = ae == null ? "" : arithmeticExpression(ae);
    final Commodity c = p.commodity();
    final String commodity = c == null ? "" : commodity(c);
    int comp = 0;
    if (p.flag() != null) {
      print(pw, p.flag());
      comp = p.flag().flag().length();
    }
    if (num.isEmpty() && commodity.isEmpty()) {
      pw.print(account);
    } else {
      final int col = currencyColumn - account.length() - currentDent - 1 /* " " */ - 2 - comp;
      final String format = "%s %" + Math.max(col, 1) + "s %s";
      pw.format(format, account, num, commodity);
    }
    final CostSpec cs = p.costSpec();
    if (cs != null) {
      space(pw);
      print(pw, cs);
    }
    final PriceAnnotation pa = p.priceAnnotation();
    if (pa != null) {
      space(pw);
      print(pw, pa);
    }
    print(pw, this::nl, p.metadata());
  }

  public void print(PrintWriter pw, PriceAnnotation pa) {
    if (pa.totalCost()) {
      pw.print("@@");
    } else {
      pw.print('@');
    }
    if (pa.priceExpression() != null) {
      space(pw);
      print(pw, pa.priceExpression());
    }
    if (pa.commodity() != null) {
      space(pw);
      print(pw, pa.commodity());
    }
  }

  private void print(PrintWriter pw, CostSpec cs) {
    if (cs.doubleBraces()) {
      pw.print("{{");
    } else {
      pw.print('{');
    }
    loop(
        cs.components(),
        ccv -> {
          if (ccv instanceof CompoundAmount ca) {
            boolean space = false;
            final CompoundExpression ce = ca.compoundExpression();
            if (ce != null) {
              space = true;
              if (ce instanceof UnaryCompoundExpression uce) {
                print(pw, uce.expression());
              } else if (ce instanceof BinaryCompoundExpression uce) {
                final ArithmeticExpression le = uce.leftExpression();
                if (le != null) {
                  print(pw, le);
                  space(pw);
                }
                pw.print('#');
                final ArithmeticExpression re = uce.rightExpression();
                if (re != null) {
                  space(pw);
                  print(pw, re);
                }
              } else {
                Assert.shouldNeverHappen();
              }
            }
            if (ca.commodity() != null) {
              if (space) {
                space(pw);
              }
              print(pw, ca.commodity());
            }
          } else if (ccv instanceof DateValue dv) {
            print(pw, dv);
          } else if (ccv instanceof StringValue sv) {
            print(pw, sv);
          }
        },
        () -> pw.print(", "));
    if (cs.doubleBraces()) {
      pw.print("}}");
    } else {
      pw.print('}');
    }
  }

  private void print(PrintWriter pw, Flag flag) {
    pw.print(flag.flag());
  }

  public void print(PrintWriter pw, Tag tag) {
    pw.print('#');
    pw.print(tag.tag());
  }

  public void print(PrintWriter pw, Link link) {
    pw.print('^');
    pw.print(link.link());
  }

  public void print(
      PrintWriter pw, Consumer<PrintWriter> beforeIfPresent, Collection<TagOrLink> tagsAndLinks) {
    if (!tagsAndLinks.isEmpty()) {
      beforeIfPresent.accept(pw);
    }
    print(pw, tagsAndLinks);
  }

  public void print(PrintWriter pw, Collection<TagOrLink> tagsAndLinks) {
    if (tagsAndLinks.isEmpty()) {
      return;
    }
    loop(
        tagsAndLinks,
        tagOrLink -> {
          if (tagOrLink instanceof Tag tag) {
            print(pw, tag);
          } else if (tagOrLink instanceof Link link) {
            print(pw, link);
          }
        },
        () -> space(pw));
  }

  private void print(PrintWriter pw, Metadata m, Consumer<PrintWriter> afterIfPresent) {
    print(pw, m);
    if (!m.metadata().isEmpty()) {
      afterIfPresent.accept(pw);
    }
  }

  private void print(PrintWriter pw, Consumer<PrintWriter> beforeIfPresent, Metadata m) {
    if (!m.metadata().isEmpty()) {
      beforeIfPresent.accept(pw);
    }
    print(pw, m);
  }

  private void print(PrintWriter pw, Metadata m) {
    final List<MetadataLine> metadata = m.metadata();
    if (metadata.isEmpty()) {
      return;
    }
    indent();
    dent(pw);
    loop(
        metadata,
        l -> {
          if (l instanceof MetadataItem mi) {
            print(pw, mi.key());
            final MetadataValue mv = mi.value();
            if (mv != null && !mv.empty()) {
              space(pw); // Space between colon and value
              print(pw, mv);
            }
          } else if (l instanceof TagValue tv) {
            print(pw, tv);
          } else if (l instanceof LinkValue lv) {
            print(pw, lv);
          } else if (l == null) {
            // TODO, Currently a comment; do nothing
            pw.print(';');
          } else {
            Assert.shouldNeverHappen();
          }
        },
        () -> {
          nl(pw);
          dent(pw);
        });
    dedent();
  }

  private String arithmeticExpression(ArithmeticExpression ae) {
    if (ae instanceof ConstantExpression ce) {
      return ce.value().toString();
    } else if (ae instanceof NegationExpression ne) {
      return "-" + arithmeticExpression(ne.expression());
    } else if (ae instanceof ParenthesisedExpression pe) {
      return "(" + arithmeticExpression(pe.expression()) + ")";
    } else if (ae instanceof PlusExpression pe) {
      return "+" + arithmeticExpression(pe.expression());
    } else if (ae instanceof AdditionExpression additionExpression) {
      return arithmeticExpression(additionExpression.leftExpression())
          + " + "
          + arithmeticExpression(additionExpression.rightExpression());
    } else if (ae instanceof SubtractionExpression se) {
      return arithmeticExpression(se.leftExpression())
          + " - "
          + arithmeticExpression(se.rightExpression());
    } else if (ae instanceof MultiplicationExpression me) {
      return arithmeticExpression(me.leftExpression())
          + " * "
          + arithmeticExpression(me.rightExpression());
    } else if (ae instanceof DivisionExpression de) {
      return arithmeticExpression(de.leftExpression())
          + " / "
          + arithmeticExpression(de.rightExpression());
    } else {
      Assert.shouldNeverHappen();
    }
    return null;
  }

  private void print(PrintWriter pw, MetadataKey mk) {
    pw.print(mk.key());
    pw.print(':');
  }

  private void print(PrintWriter pw, BooleanValue bv) {
    pw.print(String.valueOf(bv.value()).toUpperCase(Locale.ROOT));
  }

  private void print(PrintWriter pw, DateValue dv) {
    pw.print(date(dv.date()));
  }

  private void print(PrintWriter pw, StringValue sv) {
    quote(pw);
    pw.write(sv.value());
    quote(pw);
  }

  private void print(PrintWriter pw, LinkValue lv) {
    pw.write('^');
    pw.write(lv.link());
  }

  private void print(PrintWriter pw, TagValue tv) {
    pw.write('#');
    pw.write(tv.tag());
  }

  private void print(PrintWriter pw, Commodity c) {
    pw.write(commodity(c));
  }

  private void print(PrintWriter pw, Account a) {
    pw.write(account(a));
  }

  private void print(PrintWriter pw, ArithmeticExpression ae) {
    if (ae instanceof ConstantExpression ce) {
      pw.print(ce.value().toString());
    } else if (ae instanceof NegationExpression ne) {
      pw.print('-');
      print(pw, ne.expression());
    } else if (ae instanceof ParenthesisedExpression pe) {
      pw.write('(');
      print(pw, pe.expression());
      pw.write(')');
    } else if (ae instanceof PlusExpression pe) {
      pw.print('+');
      print(pw, pe.expression());
    } else if (ae instanceof AdditionExpression additionExpression) {
      print(pw, additionExpression.leftExpression());
      pw.print(" + ");
      print(pw, additionExpression.rightExpression());
    } else if (ae instanceof SubtractionExpression se) {
      print(pw, se.leftExpression());
      pw.print(" - ");
      print(pw, se.rightExpression());
    } else if (ae instanceof MultiplicationExpression me) {
      print(pw, me.leftExpression());
      pw.print(" * ");
      print(pw, me.rightExpression());
    } else if (ae instanceof DivisionExpression de) {
      print(pw, de.leftExpression());
      pw.print(" / ");
      print(pw, de.rightExpression());
    } else {
      Assert.shouldNeverHappen();
    }
  }

  private void print(PrintWriter pw, MetadataValue mv) {
    if (mv instanceof Amount a) {
      pw.print(arithmeticExpression(a.expression()) + " " + commodity(a.commodity()));
    } else if (mv instanceof ArithmeticExpression ae) {
      print(pw, ae);
    } else if (mv instanceof BooleanValue bv) {
      print(pw, bv);
    } else if (mv instanceof Commodity c) {
      print(pw, c);
    } else if (mv instanceof DateValue dv) {
      print(pw, dv);
    } else if (mv instanceof LinkValue lv) {
      print(pw, lv);
    } else if (mv instanceof NilValue) {
      // Do nothing.
    } else if (mv instanceof StringValue sv) {
      print(pw, sv);
    } else if (mv instanceof TagValue tv) {
      print(pw, tv);
    } else if (mv instanceof Account a) {
      print(pw, a);
    } else {
      Assert.shouldNeverHappen();
    }
  }
}
