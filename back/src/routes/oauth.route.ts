import { FastifyInstance, FastifyReply, FastifyRequest } from "fastify";
import { FastifyPluginOptions } from "fastify/types/plugin";

import { FastifyPluginDoneFunction } from "../types/global.types";
import {
  googleOauthQueryValidator,
  spotifyOauthQueryValidator,
  githubOauthQueryValidator,
} from "../schema/oauth.schema";
import {
  GoogleOauthBody,
  SpotifyOauthBody,
  GithubOauthBody,
} from "../types/body/oauthRequestBody.types";
import * as SecurityHelper from "../helpers/security.helper";
import * as ErrorHelper from "../helpers/error.helpers";
import { google } from "googleapis";
import ENV from "../env";
import SpotifyWebApi from "spotify-web-api-node";
import authentificationMiddleware from "../middlewares/authentification.middleware";
import { TokenService } from "../services";
import httpStatus from "http-status";
import axios from "axios";

type GoogleOauthRequest = FastifyRequest<{
  Body: GoogleOauthBody;
}>;

type SpotifyOauthRequest = FastifyRequest<{
  Body: SpotifyOauthBody;
}>;

type GithubOauthRequest = FastifyRequest<{
  Body: GithubOauthBody;
}>;

export default (
  instance: FastifyInstance,
  _opts: FastifyPluginOptions,
  done: FastifyPluginDoneFunction,
): void => {
  instance.post(
    "/google",
    { onRequest: [authentificationMiddleware()] },
    async (req: GoogleOauthRequest, res: FastifyReply) => {
      if (!googleOauthQueryValidator(req.body)) ErrorHelper.throwBodyError();

      const userInfos = SecurityHelper.getUserInfos(req);

      const code = req.body.code;

      const oauthClient = new google.auth.OAuth2(
        ENV.googleClientId,
        ENV.googleClientSecret,
        ENV.googleRedirectUrl,
      );

      const tokens = (await oauthClient.getToken(code)).tokens;

      const tokenTable = await TokenService.setGoogleToken(
        userInfos.id,
        tokens.refresh_token,
      );

      res.status(httpStatus.OK).send(tokenTable);
    },
  );
  instance.post(
    "/github",
    { onRequest: [authentificationMiddleware()] },
    async (req: GithubOauthRequest, res: FastifyReply) => {
      if (!githubOauthQueryValidator(req.body)) ErrorHelper.throwBodyError();

      const userInfos = SecurityHelper.getUserInfos(req);

      const body = {
        client_id: ENV.githubClientId,
        client_secret: ENV.githubClientSecret,
        code: req.body.code,
      };
      const opts = { headers: { accept: "application/json" } };

      const tokens = await axios.post(
        "https://github.com/login/oauth/access_token",
        body,
        opts,
      );

      const tokenTable = await TokenService.setGithubToken(
        userInfos.id,
        tokens.data.access_token,
      );

      res.status(httpStatus.OK).send(tokenTable);
    },
  );
  instance.get("/spotify", (req: FastifyRequest, res: FastifyReply) => {
    const rootUrl = "https://accounts.spotify.com/authorize";
    const options = {
      client_id: ENV.spotifyClientId,
      response_type: "code",
      redirect_uri: ENV.spotifyRedirectUrl,
      scope: [
        "user-read-private",
        "user-read-email",
        "user-modify-playback-state",
        "user-read-playback-position",
        "user-read-recently-played",
        "playlist-read-private",
        "user-read-currently-playing",
        "user-library-modify",
        "playlist-modify-private",
        "playlist-modify-public",
        "user-library-read",
      ].join(" "),
    };

    const qs = new URLSearchParams(options);

    res.status(httpStatus.OK).send(`${rootUrl}?${qs.toString()}`);
  });
  instance.get("/google", (req: FastifyRequest, res: FastifyReply) => {
    const rootUrl = "https://accounts.google.com/o/oauth2/v2/auth";

    const options = {
      redirect_uri: ENV.googleRedirectUrl,
      client_id: ENV.googleClientId,
      access_type: "offline",
      response_type: "code",
      prompt: "consent",
      scope: [
        "https://www.googleapis.com/auth/userinfo.profile",
        "https://www.googleapis.com/auth/userinfo.email",
        "https://www.googleapis.com/auth/youtube.readonly",
        "https://www.googleapis.com/auth/youtube",
        "https://www.googleapis.com/auth/youtube.upload",
      ].join(" "),
    };
    const qs = new URLSearchParams(options);

    res.status(httpStatus.OK).send(`${rootUrl}?${qs.toString()}`);
  });

  instance.post(
    "/spotify",
    { onRequest: [authentificationMiddleware()] },
    async (req: SpotifyOauthRequest, res: FastifyReply) => {
      if (!spotifyOauthQueryValidator(req.body)) ErrorHelper.throwBodyError();

      const userInfos = SecurityHelper.getUserInfos(req);

      const code = req.body.code;
      var spotifyApi = new SpotifyWebApi({
        clientId: ENV.spotifyClientId,
        clientSecret: ENV.spotifyClientSecret,
        redirectUri: ENV.spotifyRedirectUrl,
      });

      const tokens = (await spotifyApi.authorizationCodeGrant(code)).body;

      spotifyApi.setAccessToken(tokens.access_token);
      spotifyApi.setRefreshToken(tokens.refresh_token);

      const tokenTable = await TokenService.setSpotifyToken(
        userInfos.id,
        tokens.refresh_token,
      );

      res.status(httpStatus.OK).send(tokenTable);
    },
  );
  instance.get("/github", (req: FastifyRequest, res: FastifyReply) => {
    const rootUrl = "https://github.com/login/oauth/authorize";

    const options = {
      redirect_uri: ENV.githubRedirectUrl,
      client_id: ENV.githubClientId,
    };
    const qs = new URLSearchParams(options);

    res.status(httpStatus.OK).send(`${rootUrl}?${qs.toString()}`);
  });

  done();
};

export const autoPrefix = "/oauth";
