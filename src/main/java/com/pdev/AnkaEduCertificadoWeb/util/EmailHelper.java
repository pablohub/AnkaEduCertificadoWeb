package com.pdev.AnkaEduCertificadoWeb.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

@Component
public class EmailHelper {

    @Autowired
    private TemplateEngine templateEngine;

    public String generarHtmlFromThymeleafTemplate(String thymeleafTemplateName, Map<String, Object> templateModel){
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        return templateEngine.process(thymeleafTemplateName, thymeleafContext);
    }

}
