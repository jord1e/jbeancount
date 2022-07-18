package com.yuvalshavit.antlr4;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Queue;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

public abstract class DenterHelper {
  private final Queue<Token> dentsBuffer = new ArrayDeque<>();
  private final Deque<Integer> indentations = new ArrayDeque<>();
  private final int nlToken;
  private final int indentToken;
  private final int dedentToken;
  private final int commentToken;
  private boolean reachedEof;
  private final EofHandler eofHandler = new StandardEofHandler();
  private Token nextIndent = null; // MODIFIED
  private Token queuedNewlineAfterComment = null;

  public DenterHelper(int nlToken, int indentToken, int dedentToken, int commentToken) {
    this.nlToken = nlToken;
    this.indentToken = indentToken;
    this.dedentToken = dedentToken;
    this.commentToken = commentToken;
  }

  public Token nextToken() {
    initIfFirstRun();
    if (queuedNewlineAfterComment != null) {
      Token tempReturn = handleNewlineToken(queuedNewlineAfterComment);
      queuedNewlineAfterComment = null;
      return tempReturn;
    }
    if (nextIndent != null) { // MODIFIED
      Token tempReturn = nextIndent; // MODIFIED
      nextIndent = null; // MODIFIED
      return tempReturn; // MODIFIED
    } // MODIFIED
    Token t = dentsBuffer.isEmpty() ? pullToken() : dentsBuffer.remove();
    if (reachedEof) {
      return t;
    }
    final Token r;
    if (t.getType() == commentToken) {
      String text = t.getText();
      String withoutTrailingSpaces = text.replaceAll(" +(\r\n|\r|\n)?$", "$1");
      int l = withoutTrailingSpaces.length();
      String eolPlusMaybeNextIndentWs = null;
      if (l > 0) {
        if (withoutTrailingSpaces.charAt(l - 1) == '\n') {
          eolPlusMaybeNextIndentWs = "\n";
        }
        if (withoutTrailingSpaces.charAt(l - 2) == '\r') {
          if (eolPlusMaybeNextIndentWs != null) {
            eolPlusMaybeNextIndentWs = "\r\n";
          } else {
            eolPlusMaybeNextIndentWs = "\r";
          }
        }
      }
      if (eolPlusMaybeNextIndentWs == null) {
        throw new IllegalStateException("Comments should have trailing newline characters");
      }
      if (text.length() != l) {
        eolPlusMaybeNextIndentWs =
            text.substring(withoutTrailingSpaces.length() - eolPlusMaybeNextIndentWs.length());
      }
      r = new FakeCommentBeforeEolToken(t, eolPlusMaybeNextIndentWs);
      queuedNewlineAfterComment = new FakeEolAfterCommentToken(t, eolPlusMaybeNextIndentWs);
    } else if (t.getType() == nlToken) {
      r = handleNewlineToken(t);
    } else if (t.getType() == Token.EOF) {
      r = eofHandler.apply(t);

    } else {
      r = t;
    }
    return r;
  }

  public class FakeEolAfterCommentToken extends CommonToken {
    public FakeEolAfterCommentToken(Token oldToken, String nl) {
      super(oldToken);

      text = Objects.requireNonNull(nl, "nl");
      type = DenterHelper.this.nlToken;

      // When the original token is "; test\r\n" we want to start at the position after where ";
      // test" ends.
      start = super.stop - nl.length();

      // ab ; test\r\n
      // 0123456789 10    <- oldToken.getCharPositionInLine() would return 3
      //    1234567 8     <- oldToken.getText().length() would return 8
      //          ^^^^    <- We want the starting position of \r\n (so 9)
      // oldToken.getCharPositionInLine()-nl.length()=8-2=6
      // TODO
      charPositionInLine = oldToken.getCharPositionInLine() - nl.length();
    }
  }

  public class FakeCommentBeforeEolToken extends CommonToken {
    public FakeCommentBeforeEolToken(Token oldToken, String nl) {
      super(oldToken);
      Objects.requireNonNull(nl, "nl");

      // ; test\r\n
      // ^^^^^^          <- We want this part
      text = oldToken.getText().substring(0, oldToken.getText().length() - nl.length());

      stop = oldToken.getStopIndex() - nl.length();
    }
  }

  protected abstract Token pullToken();

  private void initIfFirstRun() {
    if (indentations.isEmpty()) {
      indentations.push(0);
      // First invocation. Look for the first non-NL. Enqueue it, and possibly an indentation if
      // that non-NL
      // token doesn't start at char 0.
      Token firstRealToken;
      do {
        firstRealToken = pullToken();
      } while (firstRealToken.getType() == nlToken);

      if (firstRealToken.getCharPositionInLine() > 0) {
        indentations.push(firstRealToken.getCharPositionInLine());
        dentsBuffer.add(createToken(indentToken, firstRealToken));
      }
      dentsBuffer.add(firstRealToken);
    }
  }

  private Token handleNewlineToken(Token t) {
    // fast-forward to the next non-NL
    Token nextNext = pullToken();
    while (nextNext.getType() == nlToken) {
      t = nextNext;
      nextNext = pullToken();
    }
    if (nextNext.getType() == Token.EOF) {
      return eofHandler.apply(nextNext);
    }
    // nextNext is now a non-NL token; we'll queue it up after any possible dents

    String nlText = t.getText();
    int indent =
        nlText.length() - 1; // every NL has one \n char, so shorten the length to account for it
    if (indent > 0 && nlText.charAt(0) == '\r') {
      --indent; // If the NL also has a \r char, we should account for that as well
    }
    int prevIndent = indentations.peek();
    Token r; // MODIFIED
    if (indent == prevIndent) {
      r = t; // just a newline
    } else if (indent > prevIndent) {
      r = createToken(indentToken, t);
      nextIndent = r; // MODIFIED
      r = t; // MODIFIED
      indentations.push(indent);
    } else {
      r = unwindTo(indent, t);
    }
    dentsBuffer.add(nextNext);
    return r;
  }

  private final class StandardEofHandler implements EofHandler {
    @Override
    public Token apply(Token t) {
      Token r;
      // when we reach EOF, unwind all indentations. If there aren't any, insert a NL. This lets the
      // grammar treat
      // un-indented expressions as just being NL-terminated, rather than NL|EOF.
      if (indentations.isEmpty()) {
        r = createToken(nlToken, t);
        dentsBuffer.add(t);
      } else {
        r = unwindTo(0, t);
        dentsBuffer.add(t);
      }
      reachedEof = true;
      return r;
    }
  }

  private Token createToken(int tokenType, Token copyFrom) {
    String tokenTypeStr;
    if (tokenType == nlToken) {
      tokenTypeStr = "newline";
    } else if (tokenType == indentToken) {
      tokenTypeStr = "indent";
    } else if (tokenType == dedentToken) {
      tokenTypeStr = "dedent";
    } else {
      tokenTypeStr = null;
    }
    CommonToken r = new InjectedToken(copyFrom, tokenTypeStr);
    r.setType(tokenType);
    return r;
  }

  /**
   * Returns a DEDENT token, and also queues up additional DEDENTS as necessary.
   *
   * @param targetIndent the "size" of the indentation (number of spaces) by the end
   * @param copyFrom the triggering token
   * @return a DEDENT token
   */
  private Token unwindTo(int targetIndent, Token copyFrom) {
    assert dentsBuffer.isEmpty() : dentsBuffer;
    dentsBuffer.add(createToken(nlToken, copyFrom));
    // To make things easier, we'll queue up ALL of the dedents, and then pop off the first one.
    // For example, here's how some text is analyzed:
    //
    //  Text          :  Indentation  :  Action         : Indents Deque
    //  [ baseline ]  :  0            :  nothing        : [0]
    //  [   foo    ]  :  2            :  INDENT         : [0, 2]
    //  [    bar   ]  :  3            :  INDENT         : [0, 2, 3]
    //  [ baz      ]  :  0            :  DEDENT x2      : [0]

    while (true) {
      int prevIndent = indentations.pop();
      if (prevIndent == targetIndent) {
        break;
      }
      if (targetIndent > prevIndent) {
        // "weird" condition above
        indentations.push(prevIndent); // restore previous indentation, since we've indented from it
        dentsBuffer.add(createToken(indentToken, copyFrom));
        break;
      }
      dentsBuffer.add(createToken(dedentToken, copyFrom));
    }
    indentations.push(targetIndent);
    return dentsBuffer.remove();
  }

  private interface EofHandler {
    Token apply(Token t);
  }

  private static class InjectedToken extends CommonToken {
    private InjectedToken(Token oldToken, String textOverride) {
      super(oldToken);
      if (textOverride != null) {
        super.text = textOverride;
      }
    }
  }
}
