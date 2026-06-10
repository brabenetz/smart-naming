package net.brabenetz.tools.smart.naming;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SmartNamingRunnerTest {

  private SmartNamingRunner smartNamingRunner = new SmartNamingRunner();

  @Test
  public void correctSingleArgUnchangedSingleFile() {
    assertThat(smartNamingRunner.correctSingleArg("My File's sibling's.png"))
        .containsExactly("My File's sibling's.png");
  }

  @Test
  public void correctSingleArgUnchangedArray() {
    assertThat(smartNamingRunner.correctSingleArg("file 1.png", "file 2.png", "testfile A (1).jpg"))
        .containsExactly("file 1.png", "file 2.png", "testfile A (1).jpg");
  }

  @Test
  public void correctSingleArgCorrectedSingleQuotesList() {
    assertThat(smartNamingRunner.correctSingleArg("'file 1.png' 'file 2.png' 'testfile A (1).jpg'"))
        .containsExactly("file 1.png", "file 2.png", "testfile A (1).jpg");
  }

  @Test
  public void correctSingleArgCorrectedSingleQuotesListWithQotesInFilename() {
    assertThat(smartNamingRunner.correctSingleArg("'My File's sibling's.png' 'file 2.png' 'testfile A (1).jpg'"))
        .containsExactly("My File's sibling's.png", "file 2.png", "testfile A (1).jpg");
  }

  @Test
  public void correctSingleArgCorrectedSingleQuotesFile() {
    assertThat(smartNamingRunner.correctSingleArg("'file 1.png'"))
        .containsExactly("file 1.png");
  }
}
