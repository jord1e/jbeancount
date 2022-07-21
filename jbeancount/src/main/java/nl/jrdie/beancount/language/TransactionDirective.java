package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class TransactionDirective
    extends AbstractDirectiveNode<TransactionDirective, TransactionDirective.Builder> {
  private final String payee;
  private final String narration;
  private final Flag flag;
  private final List<Posting> postings;

  private TransactionDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      String payee,
      String narration,
      Flag flag,
      List<Posting> postings,
      Metadata metadata,
      Comment comment) {
    super(sourceLocation, date, tagsAndLinks, metadata, comment);
    this.payee = payee;
    this.narration = narration;
    this.flag = Objects.requireNonNull(flag, "flag");
    this.postings = Objects.requireNonNull(postings, "postings");
  }

  public String payee() {
    return payee;
  }

  public String narration() {
    return narration;
  }

  public Flag flag() {
    return flag;
  }

  public List<Posting> postings() {
    return postings;
  }

  public static Builder newTransactionDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitTransactionDirective(this, context);
  }

  @Override
  public TransactionDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(
            sourceLocation(),
            date(),
            tagsAndLinks(),
            metadata(),
            payee,
            narration,
            flag,
            postings,
            comment());
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder
      extends AbstractDirectiveNode.Builder<TransactionDirective, Builder> {
    private String payee;
    private String narration;
    private Flag flag;
    private List<Posting> postings;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        String payee,
        String narration,
        Flag flag,
        List<Posting> postings,
        Comment comment) {
      super(sourceLocation, date, tagsAndLinks, metadata, comment);
      this.payee = payee;
      this.narration = narration;
      this.flag = flag;
      this.postings = postings;
    }

    @Override
    public TransactionDirective build() {
      return new TransactionDirective(
          sourceLocation(),
          date(),
          tagsAndLinks(),
          payee,
          narration,
          flag,
          postings,
          metadata(),
          comment());
    }

    public String payee() {
      return payee;
    }

    public Builder payee(String payee) {
      this.payee = payee;
      return this;
    }

    public String narration() {
      return narration;
    }

    public Builder narration(String narration) {
      this.narration = narration;
      return this;
    }

    public Flag flag() {
      return flag;
    }

    public Builder flag(Flag flag) {
      this.flag = flag;
      return this;
    }

    public List<Posting> postings() {
      return postings;
    }

    public Builder postings(List<Posting> postings) {
      this.postings = postings;
      return this;
    }
  }
}
