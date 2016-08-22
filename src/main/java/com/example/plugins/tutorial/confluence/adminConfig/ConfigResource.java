package com.example.plugins.tutorial.confluence.adminConfig;

/**
 * Created by UE0040 on 6/23/2016.
 */

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author Megan
 * @version Aug 22, 2016
 */
@Path("/")
@Named("ConfigResource")
public class ConfigResource
{
    @ComponentImport
    private final UserManager           userManager;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    private final TransactionTemplate   transactionTemplate;


    // ----------------------------------------------------------
    /**
     * Create a new ConfigResource object.
     *
     * @param userManager
     * @param pluginSettingsFactory
     * @param transactionTemplate
     */
    @Inject
    public ConfigResource(
        UserManager userManager,
        PluginSettingsFactory pluginSettingsFactory,
        TransactionTemplate transactionTemplate)
    {
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @param request
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request)
    {

        UserKey userKey = userManager.getRemoteUserKey(request);
        // isSystemAdmin supports null UserKeys, so no need for a separate null
// check
        if (!userManager.isSystemAdmin(userKey))
        {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        return Response.ok(
            transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction()
                {
                    PluginSettings settings =
                        pluginSettingsFactory.createGlobalSettings();
                    Config config = new Config();
                    config.setName((String)settings.get(Config.class.getName()
                        + ".name"));

                    String time =
                        (String)settings.get(Config.class.getName() + ".time");
                    if (time != null)
                    {
                        config.setTime(Integer.parseInt(time));
                    }
                    return config;
                }
            })).build();
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @param config
     * @param request
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final Config config, @Context HttpServletRequest request)
    {
        UserKey userKey = userManager.getRemoteUserKey(request);
        // isSystemAdmin supports null UserKeys, so no need for a separate null
// check
        if (!userManager.isSystemAdmin(userKey))
        {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction()
            {
                PluginSettings pluginSettings =
                    pluginSettingsFactory.createGlobalSettings();
                pluginSettings.put(
                    Config.class.getName() + ".name",
                    config.getName());
                pluginSettings.put(
                    Config.class.getName() + ".time",
                    Integer.toString(config.getTime()));
                return null;
            }
        });
        return Response.noContent().build();
    }


    // @Xml tags are JAXB annotations
    // -------------------------------------------------------------------------
    /**
     * Write a one-sentence summary of your class here. Follow it with
     * additional details about its purpose, what abstraction it represents, and
     * how to use it.
     *
     * @author Megan
     * @version Aug 22, 2016
     */
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Config
    {
        @XmlElement
        private String name;
        @XmlElement
        private int    time;


        // ----------------------------------------------------------
        /**
         * Place a description of your method here.
         * @return
         */
        public String getName()
        {
            return name;
        }


        // ----------------------------------------------------------
        /**
         * Place a description of your method here.
         * @param name
         */
        public void setName(String name)
        {
            this.name = name;
        }


        // ----------------------------------------------------------
        /**
         * Place a description of your method here.
         * @return
         */
        public int getTime()
        {
            return time;
        }


        // ----------------------------------------------------------
        /**
         * Place a description of your method here.
         * @param time
         */
        public void setTime(int time)
        {
            this.time = time;
        }
    }
}
