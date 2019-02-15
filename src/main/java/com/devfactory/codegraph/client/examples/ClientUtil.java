package com.devfactory.codegraph.client.examples;

import com.devfactory.codegraph.client.core.ICodegraphClient;
import com.devfactory.codegraph.client.core.impl.CodegraphClient;
import com.devfactory.codegraph.client.exceptions.CodegraphClientException;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClientUtil {

    @SneakyThrows(URISyntaxException.class)
    ICodegraphClient loginAndGetClient(String username, String password) throws CodegraphClientException {
        URI baseURI = new URI("http://rest-server.codegraph-dev.devfactory.com/api/v1.0/");
        ICodegraphClient client = new CodegraphClient(baseURI);
        client.login(username, password);
        return client;
    }

}
