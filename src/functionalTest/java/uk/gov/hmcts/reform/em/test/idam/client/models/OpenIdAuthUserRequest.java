package uk.gov.hmcts.reform.em.test.idam.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@SuppressWarnings({"ParameterName","MemberName"})
@EqualsAndHashCode
@NoArgsConstructor
@Data
public class OpenIdAuthUserRequest {

    @JsonProperty("grant_type")
    private String grantType;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("redirect_uri")
    private String redirectUri;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    public OpenIdAuthUserRequest(String grant_type, String clientId, String clientSecret,
                                 String redirectUri, String scope,
                                 String username, String password) {
        this.grantType = grant_type;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scope = scope;
        this.username = username;
        this.password = password;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private OpenIdAuthUserRequest(Builder builder) {
        this.grantType = builder.grantType;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.redirectUri = builder.redirectUri;
        this.scope = builder.scope;
        this.username = builder.username;
        this.password = builder.password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String scope;
        private String username;
        private String password;

        public Builder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public OpenIdAuthUserRequest build() {
            return new OpenIdAuthUserRequest(this);
        }
    }
}
