package nl.jrdie.beancount.language.tools;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import nl.jrdie.beancount.language.BalanceDirective;
import nl.jrdie.beancount.language.CloseDirective;
import nl.jrdie.beancount.language.Comment;
import nl.jrdie.beancount.language.CommodityDirective;
import nl.jrdie.beancount.language.CustomDirective;
import nl.jrdie.beancount.language.DocumentDirective;
import nl.jrdie.beancount.language.EventDirective;
import nl.jrdie.beancount.language.IncludePragma;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.Node;
import nl.jrdie.beancount.language.NoteDirective;
import nl.jrdie.beancount.language.OpenDirective;
import nl.jrdie.beancount.language.OptionPragma;
import nl.jrdie.beancount.language.PadDirective;
import nl.jrdie.beancount.language.PluginPragma;
import nl.jrdie.beancount.language.PriceDirective;
import nl.jrdie.beancount.language.QueryDirective;
import nl.jrdie.beancount.language.TransactionDirective;

public interface NodeVisitor {

  TraversalControl visitIncludePragma(IncludePragma ip, TraverserContext<Node<?, ?>> data);

  TraversalControl visitPluginPragma(PluginPragma pp, TraverserContext<Node<?, ?>> data);

  TraversalControl visitOptionPragma(OptionPragma op, TraverserContext<Node<?, ?>> data);

  TraversalControl visitCloseDirective(CloseDirective cd, TraverserContext<Node<?, ?>> data);

  TraversalControl visitBalanceDirective(BalanceDirective bd, TraverserContext<Node<?, ?>> data);

  TraversalControl visitCommodityDirective(
      CommodityDirective cd, TraverserContext<Node<?, ?>> data);

  TraversalControl visitCustomDirective(CustomDirective cd, TraverserContext<Node<?, ?>> data);

  TraversalControl visitDocumentDirective(DocumentDirective dd, TraverserContext<Node<?, ?>> data);

  TraversalControl visitEventDirective(EventDirective ed, TraverserContext<Node<?, ?>> data);

  TraversalControl visitNoteDirective(NoteDirective nd, TraverserContext<Node<?, ?>> data);

  TraversalControl visitOpenDirective(OpenDirective od, TraverserContext<Node<?, ?>> data);

  TraversalControl visitQueryDirective(QueryDirective qd, TraverserContext<Node<?, ?>> data);

  TraversalControl visitTransactionDirective(
      TransactionDirective td, TraverserContext<Node<?, ?>> data);

  TraversalControl visitPriceDirective(PriceDirective pd, TraverserContext<Node<?, ?>> data);

  TraversalControl visitPadDirective(PadDirective pd, TraverserContext<Node<?, ?>> data);

  TraversalControl visitJournal(Journal journal, TraverserContext<Node<?, ?>> data);

  TraversalControl visitComment(Comment comment, TraverserContext<Node<?, ?>> data);
}
