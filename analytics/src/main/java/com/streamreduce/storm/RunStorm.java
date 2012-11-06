package com.streamreduce.storm;

import com.streamreduce.storm.topology.JuggaloaderTopology;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

public class RunStorm {

    private static final String TOPOLOGY_NAME = "juggaloader";
    private static final String CLUSTER_PRODUCTION = "production";
    private static final String CLUSTER_LOCAL = "local";
    private static Logger logger = Logger.getLogger(RunStorm.class);

    public static void main(String[] args) {

        if (args == null || args.length < 1) {
            throw new RuntimeException("You need to specify a cluster mode: local | production ");
        }

        String mode = args[0];

        if (!(mode.equals(CLUSTER_LOCAL) || mode.equals(CLUSTER_PRODUCTION))) {
            throw new RuntimeException("Unknown cluster type of " + mode);
        }

        JuggaloaderTopology juggaloaderTopology = new JuggaloaderTopology();
        Config config = new Config();
        config.registerSerialization(LinkedHashMap.class);
        config.setFallBackOnJavaSerialization(false);

        if (mode.equals(CLUSTER_LOCAL)) {
            config.setDebug(true);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(TOPOLOGY_NAME, config, juggaloaderTopology.createJuggaloaderTopology());
            logger.info("Start Storm Local Cluster");
        }

        if (mode.equals(CLUSTER_PRODUCTION)) {
            config.setNumAckers(5);
            try {
                StormSubmitter.submitTopology(TOPOLOGY_NAME, config, juggaloaderTopology.createJuggaloaderTopology());
            } catch (AlreadyAliveException e) {
                logger.error(e.getMessage(), e);
            } catch (InvalidTopologyException e) {
                logger.error(e.getMessage(), e);
            }
            logger.info("Start Storm Production Cluster");
        }

        // use for all profiles
        startEmbeddedHttpServer();
    }

    public static void startEmbeddedHttpServer() {
        try {
            Server server = new Server(8193);
            server.setHandler(new AbstractHandler() {
                @Override
                public void handle(String target, HttpServletRequest request,
                                   HttpServletResponse response, int dispatch)
                        throws IOException, ServletException {
                    response.setContentType("text/html;charset=utf-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().println(TOPOLOGY_NAME);
                    ((Request) request).setHandled(true);
                }
            });
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}