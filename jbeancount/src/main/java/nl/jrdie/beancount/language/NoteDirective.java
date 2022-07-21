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
  private final String note;

  private NoteDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      Account account,
      String note,
      Metadata metadata,
      Comment comment) {
    super(sourceLocation, date, tagsAndLinks, metadata, comment);
    this.account = Objects.requireNonNull(account, "account");
    this.note = Objects.requireNonNull(note, "comment");
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
  public String note() {
    return note;
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
        new Builder(sourceLocation(), date(), tagsAndLinks(), metadata(), note, account, comment());
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder extends AbstractDirectiveNode.Builder<NoteDirective, Builder> {
    private String note;
    private Account account;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        String note,
        Account account,
        Comment comment) {
      super(sourceLocation, date, tagsAndLinks, metadata, comment);
      this.note = note;
      this.account = account;
    }

    @Override
    public NoteDirective build() {
      return new NoteDirective(
          sourceLocation(), date(), tagsAndLinks(), account, note, metadata(), comment());
    }

    public String note() {
      return note;
    }

    public Builder note(String note) {
      this.note = note;
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
