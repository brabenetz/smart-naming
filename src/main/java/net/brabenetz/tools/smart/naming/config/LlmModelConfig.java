package net.brabenetz.tools.smart.naming.config;

public class LlmModelConfig {

    private String url;
    private String model;
    private LlmAuthConfig auth;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LlmAuthConfig getAuth() {
        return auth;
    }

    public void setAuth(LlmAuthConfig auth) {
        this.auth = auth;
    }
}