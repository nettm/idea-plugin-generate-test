package com.nettm.idea.plugin.action;

import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.nettm.idea.plugin.parse.JavaClass;
import com.nettm.idea.plugin.parse.Method;
import com.nettm.idea.plugin.parse.ParseJava;
import com.nettm.idea.plugin.settings.TestSettingsState;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GenerateUnitTestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        VirtualFile vf = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (psiFile == null || psiFile.getVirtualFile() == null ||
                vf == null || !"JAVA".equals(vf.getFileType().getName())) {
            Messages.showErrorDialog("Please select a java file", "Error");
            return;
        }

        AnalysisScope scope = new AnalysisScope(psiFile);
        String name = vf.getNameWithoutExtension();
        String classPath = Objects.requireNonNull(psiFile).getVirtualFile().getPath();
        JavaClass javaClass = ParseJava.parse(name, classPath);
        String output = generateTest(javaClass);
        String outputFile = getOutputPath(project, javaClass.getPackageName(), javaClass.getJavaName());
        writeFile(outputFile, output);
    }

    private String generateTest(JavaClass javaClass) {
        Configuration conf = new Configuration(Configuration.VERSION_2_3_31);
        conf.setClassForTemplateLoading(GenerateUnitTestAction.class, "/templates");
        conf.setDefaultEncoding("UTF-8");

        String packageName = getPackageName(javaClass.getPackageName());
        String className = getJavaName(javaClass.getJavaName());
        List<String> imports = javaClass.getImportNames() == null ? new ArrayList<>() : javaClass.getImportNames();
        String interfaceName = CollectionUtils.isEmpty(javaClass.getInterfaceNames()) ? className : javaClass.getInterfaceNames().get(0);
        List<Method> methods = CollectionUtils.isEmpty(javaClass.getMethods()) ? new ArrayList<>() : javaClass.getMethods();

        Map<String, Object> templateData = new HashMap<>(8);
        templateData.put("package", packageName);
        templateData.put("className", className);
        templateData.put("imports", addImport(javaClass.getPackageName(), className, imports));
        templateData.put("interfaceName", interfaceName);
        templateData.put("methods", methods);

        String output = null;
        try (Writer out = new StringWriter()) {
            Template template = conf.getTemplate("test.ftl");
            template.process(templateData, out);
            output = out.toString();
            out.flush();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        return output;
    }

    private String getOutputPath(Project project, String packageName, String javaName) {
        String testPath = TestSettingsState.getInstance().getProjectPath();
        if (StringUtils.isBlank(testPath)) {
            return project.getBasePath() + "/src/test/java/test/" +
                    getPackageName(packageName) + "/" + getJavaName(javaName) + "Test.java";
        } else {
            if (testPath.contains("\\src")) {
                testPath = StringUtils.substringBeforeLast(testPath, "\\src");
            }
            if (testPath.endsWith("\\")) {
                testPath = StringUtils.substringBeforeLast(testPath, "\\");
            }
            return testPath + "/src/test/java/test/" +
                    getPackageName(packageName) + "/" + getJavaName(javaName) + "Test.java";
        }

    }

    private void writeFile(String filePath, String output) {
        String dir = StringUtils.substringBeforeLast(filePath, "/");
        new File(dir).mkdirs();

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(filePath))) {
            os.write(output.getBytes(StandardCharsets.UTF_8));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getJavaName(String javaName) {
        return javaName.endsWith("Impl") ? StringUtils.substringBefore(javaName, "Impl") : javaName;
    }

    private String getPackageName(String packageName) {
        String pName = packageName.endsWith(".impl") ? StringUtils.substringBefore(packageName, ".impl") : packageName;
        pName = pName.endsWith(".service") ? StringUtils.substringBefore(pName, ".service") : pName;
        pName = pName.endsWith(".controller") ? StringUtils.substringBefore(pName, ".controller") : pName;
        return StringUtils.substringAfterLast(pName, ".");
    }

    private List<String> addImport(String packageName, String className, List<String> imports) {
        for (String anImport : imports) {
            if (anImport.contains(className)) {
                return imports;
            }
        }
        imports.add(packageName + "." + className);
        return imports;
    }

}
