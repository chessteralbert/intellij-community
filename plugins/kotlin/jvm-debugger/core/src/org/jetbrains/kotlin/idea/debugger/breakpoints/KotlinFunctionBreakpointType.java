// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.debugger.breakpoints;

import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.JavaLineBreakpointTypeBase;
import com.intellij.debugger.ui.breakpoints.MethodBreakpointPropertiesPanel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.debugger.breakpoints.properties.JavaMethodBreakpointProperties;
import org.jetbrains.kotlin.idea.debugger.KotlinDebuggerCoreBundle;
import org.jetbrains.kotlin.platform.TargetPlatform;
import org.jetbrains.kotlin.psi.*;

import javax.swing.*;

import static org.jetbrains.kotlin.idea.debugger.breakpoints.BreakpointTypeUtilsKt.isBreakpointApplicable;
import static org.jetbrains.kotlin.idea.project.PlatformKt.getPlatform;
import static org.jetbrains.kotlin.platform.TargetPlatformKt.isCommon;
import static org.jetbrains.kotlin.platform.jvm.JvmPlatformKt.isJvm;

// This class is copied from com.intellij.debugger.ui.breakpoints.MethodBreakpoint.
// Changed parts are marked with '// MODIFICATION: ' comments.
// This should be deleted when IDEA opens the method breakpoint API (presumably in 193).
public class KotlinFunctionBreakpointType
        extends JavaLineBreakpointTypeBase<JavaMethodBreakpointProperties>
        implements KotlinBreakpointType {
    // MODIFICATION: Start Kotlin implementation
    public KotlinFunctionBreakpointType() {
        super("kotlin-function", KotlinDebuggerCoreBundle.message("function.breakpoint.tab.title"));
    }

    @Override
    public int getPriority() {
        return 120;
    }
    // MODIFICATION: End Kotlin implementation

    @NotNull
    @Override
    public Icon getEnabledIcon() {
        return AllIcons.Debugger.Db_method_breakpoint;
    }

    @NotNull
    @Override
    public Icon getDisabledIcon() {
        return AllIcons.Debugger.Db_disabled_method_breakpoint;
    }

    @NotNull
    @Override
    public Icon getSuspendNoneIcon() {
        return AllIcons.Debugger.Db_no_suspend_method_breakpoint;
    }

    @NotNull
    @Override
    public Icon getMutedEnabledIcon() {
        return AllIcons.Debugger.Db_muted_method_breakpoint;
    }

    @NotNull
    @Override
    public Icon getMutedDisabledIcon() {
        return AllIcons.Debugger.Db_muted_disabled_method_breakpoint;
    }

    @NotNull
    @Override
    public Icon getInactiveDependentIcon() {
        return AllIcons.Debugger.Db_dep_method_breakpoint;
    }

    @Override
    public String getShortText(XLineBreakpoint<JavaMethodBreakpointProperties> breakpoint) {
        StringBuilder buffer = new StringBuilder();
        String className = breakpoint.getProperties().myClassPattern;
        boolean classNameExists = className != null && className.length() > 0;

        if (classNameExists) {
            buffer.append(className);
        }

        if (breakpoint.getProperties().myMethodName != null) {
            if (classNameExists) {
                buffer.append(".");
            }
            buffer.append(breakpoint.getProperties().myMethodName);
        }

        @SuppressWarnings("HardCodedStringLiteral")
        String s = buffer.toString();
        return s;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public XBreakpointCustomPropertiesPanel createCustomPropertiesPanel(@NotNull Project project) {
        return new MethodBreakpointPropertiesPanel();
    }

    @Nullable
    @Override
    public JavaMethodBreakpointProperties createProperties() {
        return new JavaMethodBreakpointProperties();
    }

    @Nullable
    @Override
    public JavaMethodBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        JavaMethodBreakpointProperties properties = new JavaMethodBreakpointProperties();
        if (Registry.is("debugger.emulate.method.breakpoints")) {
            properties.EMULATED = true; // create all new emulated
        }
        if (Registry.is("debugger.method.breakpoints.entry.default")) {
            properties.WATCH_EXIT = false;
        }
        return properties;
    }

    @NotNull
    @Override
    public Breakpoint<JavaMethodBreakpointProperties> createJavaBreakpoint(Project project, XBreakpoint breakpoint) {
        return new KotlinFunctionBreakpoint(project, breakpoint);
    }

    @Override
    public boolean canBeHitInOtherPlaces() {
        return true;
    }

    // MODIFICATION: Start Kotlin implementation
    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        return isKtFileWithCommonOrJvmPlatform(file, project) && isBreakpointApplicable(file, line, project, element -> {
            if (element instanceof KtConstructor) {
                return ApplicabilityResult.DEFINITELY_YES;
            }

            if (element instanceof KtClass) {
                KtClass clazz = (KtClass) element;
                return ApplicabilityResult.maybe(
                        !(clazz instanceof KtEnumEntry)
                        && !clazz.isAnnotation()
                        && !clazz.isInterface()
                        && clazz.hasPrimaryConstructor()
                );
            }

            if (element instanceof KtFunction) {
                KtFunction function = (KtFunction) element;
                return ApplicabilityResult.maybe(
                        !KtPsiUtil.isLocal(function) && !BreakpointTypeUtilsKt.isInlineOnly(function)
                );
            }

            if (element instanceof KtPropertyAccessor) {
                KtPropertyAccessor accessor = (KtPropertyAccessor) element;
                KtProperty property = accessor.getProperty();
                return ApplicabilityResult.maybe(accessor.hasBody() && !KtPsiUtil.isLocal(property));
            }

            return ApplicabilityResult.UNKNOWN;
        });
    }

    private static boolean isKtFileWithCommonOrJvmPlatform(@NotNull VirtualFile file, @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (!(psiFile instanceof KtFile)) {
            return false;
        }

        TargetPlatform platform = getPlatform((KtFile)psiFile);
        return isCommon(platform) || isJvm(platform);
    }
    // MODIFICATION: End Kotlin implementation
}