// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.codeInspection.tests.java

import com.intellij.codeInspection.tests.JUnit5AssertionsConverterInspectionTestBase
import com.intellij.codeInspection.tests.ULanguage

class JavaJUnit5AssertionsConverterInspectionTest : JUnit5AssertionsConverterInspectionTestBase() {
  fun `test AssertArrayEquals`() {
    myFixture.testQuickFix(ULanguage.JAVA, """
      import static org.junit.Assert.*;

      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          assert<caret>ArrayEquals(new Object[] {}, null);
        }
      }
    """.trimIndent(), """
      import static org.junit.Assert.*;

      import org.junit.jupiter.api.Assertions;
      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          Assertions.assertArrayEquals(new Object[] {}, null);
        }
      }
    """.trimIndent(), "Replace with 'org.junit.jupiter.api.Assertions' method call")
  }

  fun `test AssertArrayEquals message`() {
    myFixture.testQuickFix(ULanguage.JAVA, """
      import static org.junit.Assert.*;

      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          assert<caret>ArrayEquals("message", new Object[] {}, null);
        }
      }
    """.trimIndent(), """
      import static org.junit.Assert.*;

      import org.junit.jupiter.api.Assertions;
      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          Assertions.assertArrayEquals(new Object[] {}, null, "message");
        }
      }
    """.trimIndent(), "Replace with 'org.junit.jupiter.api.Assertions' method call")
  }

  fun `test AssertEquals`() {
    myFixture.testQuickFix(ULanguage.JAVA, """
      import static org.junit.Assert.*;

      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          assert<caret>Equals("message", "Expected", "actual");
        }
      }
    """.trimIndent(), """
      import static org.junit.Assert.*;

      import org.junit.jupiter.api.Assertions;
      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          Assertions.assertEquals("Expected", "actual", "message");
        }
      }
    """.trimIndent(), "Replace with 'org.junit.jupiter.api.Assertions' method call")
  }

  fun `test AssertNotEqualsWithDelta`() {
    myFixture.testQuickFixUnavailable(ULanguage.JAVA, """
      import static org.junit.Assert.*;

      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          assert<caret>NotEquals(1, 1, 1);
        }
      }
    """.trimIndent())
  }

  fun `test AssertThat`() {
    myFixture.testQuickFix(ULanguage.JAVA, """
      import static org.junit.Assert.*;

      import org.hamcrest.Matcher;
      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst(Matcher matcher) throws Exception {
          assert<caret>That("reason", "null", matcher);
        }
      }
    """.trimIndent(), """
      import static org.junit.Assert.*;

      import org.hamcrest.Matcher;
      import org.hamcrest.MatcherAssert;
      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst(Matcher matcher) throws Exception {
          MatcherAssert.assertThat("reason", "null", matcher);
        }
      }
    """.trimIndent(), "Replace with 'org.hamcrest.MatcherAssert' method call")
  }

  fun `test AssertTrue`() {
    myFixture.testQuickFix(ULanguage.JAVA, """
      import static org.junit.Assert.*;

      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          assert<caret>True("message", false);
        }
      }
    """.trimIndent(), """
      import static org.junit.Assert.*;

      import org.junit.jupiter.api.Assertions;
      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          Assertions.assertTrue(false, "message");
        }
      }
    """.trimIndent(), "Replace with 'org.junit.jupiter.api.Assertions' method call")
  }

  fun `test AssertTrue method reference`() {
    myFixture.testQuickFix(ULanguage.JAVA, """
      import org.junit.Assert;
      import org.junit.jupiter.api.Test;
      
      import java.util.ArrayList;
      import java.util.List;
      
      class Test1 {
        @Test
        public void testFirst() throws Exception {
          List<Boolean> booleanList = new ArrayList<>();
          booleanList.forEach(Assert::assert<caret>True);
        }
      }
    """.trimIndent(), """
      import org.junit.Assert;
      import org.junit.jupiter.api.Assertions;
      import org.junit.jupiter.api.Test;

      import java.util.ArrayList;
      import java.util.List;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          List<Boolean> booleanList = new ArrayList<>();
          booleanList.forEach(Assertions::assertTrue);
        }
      }
    """.trimIndent(), "Replace with 'org.junit.jupiter.api.Assertions' method call")
  }

  fun `test AssumeTrue`() {
    myFixture.testQuickFix(ULanguage.JAVA, """
      import static org.junit.Assume.*;

      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          assume<caret>True("message", false);
        }
      }
    """.trimIndent(), """
      import static org.junit.Assume.*;

      import org.junit.jupiter.api.Assumptions;
      import org.junit.jupiter.api.Test;

      class Test1 {
        @Test
        public void testFirst() throws Exception {
          Assumptions.assumeTrue(false, "message");
        }
      }
    """.trimIndent(), "Replace with 'org.junit.jupiter.api.Assumptions' method call")
  }
}