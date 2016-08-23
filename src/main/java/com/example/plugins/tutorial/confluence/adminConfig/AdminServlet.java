package com.example.plugins.tutorial.confluence.adminConfig;

import javax.inject.Inject;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.inject.Named;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author Megan
 * @version Aug 17, 2016
 */
@Named("AdminServlet")
public class AdminServlet
    extends HttpServlet
{
    @ComponentImport
    private final UserManager      userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final TemplateRenderer templateRenderer;


    // ----------------------------------------------------------
    /**
     * Create a new AdminServlet object.
     *
     * @param userManager
     * @param loginUriProvider
     * @param templateRenderer
     */
    @Inject
    public AdminServlet(
        UserManager userManager,
        LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer)
    {
        super();
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;

    }


    // ----------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException,
        IOException
    {
        if (!userManager.isAdmin(userManager.getRemoteUserKey()))
        {
            redirectToLogin(request, response);
            return;
        }

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("/templates/admin.vm", response.getWriter());
    }


    private void redirectToLogin(
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException
    {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request))
            .toASCIIString());
    }


    private URI getUri(HttpServletRequest request)
    {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null)
        {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }

}
