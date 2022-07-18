package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class NoteDirective
    extends AbstractDirectiveNode<NoteDirective, NoteDirective.Builder> {

  private final Account account;
  private final String comment;

  private NoteDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      Account account,
      String comment,
      Metadata metadata) {
    super(sourceLocation, date, tagsAndLinks, metadata);
    this.account = Objects.requireNonNull(account, "account");
    this.comment = Objects.requireNonNull(comment, "comment");
  }

  /**
   * The account this note corresponds to.
   *
   * @return The {@link Account} this note belongs to, never null
   */
  public Account account() {
    return account;
  }

  /**
   * The text of the note.
   *
   * @return The text of the note, never null
   */
  public String comment() {
    return comment;
  }

  public static NoteDirective.Builder newNoteDirective() {
    return new NoteDirective.Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitNoteDirective(this, context);
  }

  @Override
  public NoteDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(sourceLocation(), date(), tagsAndLinks(), metadata(), comment, account);
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder extends AbstractDirectiveNode.Builder<NoteDirective, Builder> {
    private String comment;
    private Account account;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        String comment,
        Account account) {
      super(sourceLocation, date, tagsAndLinks, metadata);
      this.comment = comment;
      this.account = account;
    }

    @Override
    public NoteDirective build() {
      return new NoteDirective(
          sourceLocation(), date(), tagsAndLinks(), account, comment, metadata());
    }

    public String comment() {
      return comment;
    }

    public Builder comment(String comment) {
      this.comment = comment;
      return this;
    }

    public Account account() {
      return account;
    }

    public Builder account(Account account) {
      this.account = account;
      return this;
    }
  }
}
