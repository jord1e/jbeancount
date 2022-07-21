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
import nl.jrdie.beancount.language.Posting;
import nl.jrdie.beancount.language.PriceDirective;
import nl.jrdie.beancount.language.QueryDirective;
import nl.jrdie.beancount.language.TransactionDirective;

public class NodeVisitorStub implements NodeVisitor {
  @Override
  public TraversalControl visitIncludePragma(IncludePragma ip, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitPluginPragma(PluginPragma pp, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitOptionPragma(OptionPragma op, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitCloseDirective(
      CloseDirective cd, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitBalanceDirective(
      BalanceDirective bd, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitCommodityDirective(
      CommodityDirective cd, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitCustomDirective(
      CustomDirective cd, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitDocumentDirective(
      DocumentDirective dd, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitEventDirective(
      EventDirective ed, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitNoteDirective(NoteDirective nd, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitOpenDirective(OpenDirective od, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitQueryDirective(
      QueryDirective qd, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitTransactionDirective(
      TransactionDirective td, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitPriceDirective(
      PriceDirective pd, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitPadDirective(PadDirective pd, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitJournal(Journal journal, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitComment(Comment comment, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public TraversalControl visitPosting(Posting posting, TraverserContext<Node<?, ?>> data) {
    return TraversalControl.CONTINUE;
  }
}
