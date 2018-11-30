Service account method (YouTube), left for implementation example:

    private String getToken() {
        String rawPrivateKey = userInfo.getPrivateKey()
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            byte[] pkcs8EncodedBytes = Base64.decode(rawPrivateKey.getBytes());
            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(keySpecPKCS8);
            Algorithm algorithm = Algorithm.RSA256(null, privateKey);

            final Map<String, Object> headers = new HashMap<>();
            headers.put("alg", "RS256");
            headers.put("typ", "JWT");
            long currentTime = currentTimeMillis();
            String jwt = JWT.create()
                    .withHeader(headers)
                    .withIssuer(userInfo.getClientEmail())
                    .withClaim("scope", "https://www.googleapis.com/auth/youtube.force-ssl")
                    .withAudience(GOOGLE_TOKEN_REQUEST_URL)
                    //TODO: Fix time offset problem
                    .withClaim("exp", new Date(currentTime + 2000000))
                    .withClaim("iat", new Date(currentTime - 300000))
                    .sign(algorithm);

            Form input = new Form();
            input.param("grant_type", GRANT_TYPE_VALUE);
            input.param("assertion", jwt);
            Entity<Form> entity = Entity.entity(input, MediaType.APPLICATION_FORM_URLENCODED);
            Token response = client.target(GOOGLE_TOKEN_REQUEST_URL)
                    .request().header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED)
                    .post(entity).readEntity(Token.class);
            return response.getAccessToken();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        throw new BadRequestException("Failed to acquire token");
    }

Requires:

        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
            <version>3.3.0</version>
        </dependency>