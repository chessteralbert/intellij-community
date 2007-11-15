/*
 * Copyright 2000-2007 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions;

import com.intellij.psi.PsiType;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrStatement;

/**
 * @author ilyas
 */
public interface GrExpression extends GrStatement, PsiAnnotationMemberValue {
  GrExpression[] EMPTY_ARRAY = new GrExpression[0];

  @Nullable
  PsiType getType();

  @Nullable
  PsiType getNominalType();

  GrExpression replaceWithExpression(GrExpression expression,
                                     boolean removeUnnecessaryParentheses) throws IncorrectOperationException;
}
