package com.devfactory.codegraph.client.examples;

import com.devfactory.codegraph.client.core.ICodegraphClient;
import com.devfactory.codegraph.client.exceptions.CodegraphClientException;
import com.devfactory.codegraph.client.models.Build;
import com.devfactory.codegraph.client.models.Language;
import com.devfactory.codegraph.client.models.Request;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckTokenExpiration {
    public static final Integer TIMEOUT_SECONDS = 190;
    public static void main(String[] args) throws CodegraphClientException, URISyntaxException, InterruptedException {

        if (args.length != 2) {
            printUsage();
            return;
        }

        ICodegraphClient client1 = ClientUtil.loginAndGetClient(args[0], args[1]);
        log.info("Token1: {}", client1.getSession().getToken());

        Request request = Request.builder()
                .language(Language.JAVA)
                .sourceLocation(new URI("https://github.com/AlexanderMelnichukAurea/QuickSearch.git"))
                .branch("master")
                .commit("c5d8203363521d9c7bea2e351762aaded985d8ff")
                .runPdg(false)
                .build();

        Build build = client1.triggerBuild(request);

        String requestId = build.getRequestId();

        log.info("Build triggered by request id {}", requestId);

        Build requestedBuild = client1.getBuild(requestId);
        log.info("Build requested by id {} successfully: {}", requestId, requestedBuild);
        log.info("Waiting for {} seconds...", TIMEOUT_SECONDS);

        Thread.sleep(TIMEOUT_SECONDS * 1000);

        log.info("{} seconds passed", TIMEOUT_SECONDS);

        ICodegraphClient client2 = ClientUtil.loginAndGetClient(args[0], args[1]);
        log.info("New token obtained (token2): {}, Account2: {}", client2.getSession().getToken(),
                client2.getSession().getAccount());

        if (client1.getSession().getToken().equals(client2.getSession().getToken())) {
            log.error("The token was not refreshed in the timeout of {} seconds. Need to reduce timeout value "
                    + "(CODEGRAPH_AUTH_TOKEN_TTL) on rest-server. No reason to continue the test.", TIMEOUT_SECONDS);
            log.error("FAILURE");
            return;
        }

        log.info("Old token (token1): {}, Account1: {}", client1.getSession().getToken(),
                client1.getSession().getAccount());
        log.info("Trying to request build with token1...");

        requestedBuild = client1.getBuild(requestId);

        log.info("Build requested with token1 by id {} successfully: {}", requestId, requestedBuild);
        log.info("New value of token1: {}", client1.getSession().getToken());

        log.info("SUCCESS");
    }

    private static void printUsage() {
        System.out.println("Usage: \n java -jar codegraph-check-token-expiration.jar <username> <password>");
    }
}
