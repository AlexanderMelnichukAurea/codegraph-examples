package com.devfactory.codegraph.client.examples;

import com.devfactory.codegraph.client.core.ICodegraphClient;
import com.devfactory.codegraph.client.exceptions.CodegraphClientException;
import com.devfactory.codegraph.client.models.Build;
import com.devfactory.codegraph.client.models.Language;
import com.devfactory.codegraph.client.models.Request;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckS3AccessError {

    public static void main(String[] args) throws CodegraphClientException, URISyntaxException {

        if (args.length != 2) {
            printUsage();
            return;
        }

        ICodegraphClient client = ClientUtil.loginAndGetClient(args[0], args[1]);

        log.info("Token1: {}", client.getSession().getToken());

        Request request = Request.builder()
                .language(Language.JAVA)
                .sourceLocation(new URI("s3://codegraph-dev/sources/brp-test-repo-master-2.zip"))
                .branch(null)
                .commit("commit")
                .runPdg(false)
                .build();

        Build build = client.triggerBuild(request);

        log.info("Build with accessible S3 source triggered by request id {}", build.getRequestId());

        Request request2 = Request.builder()
                .language(Language.JAVA)
                .sourceLocation(new URI("s3://ayuschenko/brp-test-repo-master-3.zip"))
                .branch(null)
                .commit("commit")
                .runPdg(false)
                .build();

        Build build2;
        try {
            build2 = client.triggerBuild(request2);
        } catch (CodegraphClientException e) {
            log.info("CodegraphClientException is thrown as expected.");
            StringWriter stacktrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stacktrace));
            log.info("Stacktrace: \n{}", stacktrace);
            log.info("SUCCESS: Build with inaccessible source was not triggered, CodegraphClientException was thrown.");

            return;
        }

        log.info("FAILURE: Build with inaccessible source was triggered by request id {}", build2.getRequestId());

    }

    private static void printUsage() {
        System.out.println("Usage: \n java -jar CheckS3AccessError <username> <password>");
    }
}
