import { Area } from "@prisma/client";
import SpotifyWebApi from "spotify-web-api-node";
import { AreaService, TokenService } from "../../services";
import ENV from "../../env";
import * as ServiceHelper from "../../helpers/service.helpers";

const checkMusicSkip = async (area: Area): Promise<string | null> => {
  var spotifyApi = new SpotifyWebApi({
    clientId: ENV.spotifyClientId,
    clientSecret: ENV.spotifyClientSecret,
  });

  const refreshToken = await TokenService.getSpotifyToken(area.userId);

  if (refreshToken == null) return null;
  spotifyApi.setRefreshToken(refreshToken);

  const accessToken = await (await spotifyApi.refreshAccessToken()).body;

  spotifyApi.setAccessToken(accessToken.access_token);

  const currentSong = (await spotifyApi.getMyCurrentPlayingTrack()).body;

  if (currentSong.item?.name == undefined) return null;
  if (area.lastActionValue == null) {
    await AreaService.updateAreaValues(area.id, currentSong.item?.name);
    return null;
  }
  const params = {
    songName: currentSong.item.name,
  };

  if (currentSong.item.name != area.lastActionValue) {
    await AreaService.updateAreaValues(area.id, currentSong.item.name);
    return ServiceHelper.injectParamInReaction<typeof params>(
      area.reactionParam,
      params,
    );
  }
  return null;
};

const checkIsMusicLiked = async (area: Area): Promise<string | null> => {
  var spotifyApi = new SpotifyWebApi({
    clientId: ENV.spotifyClientId,
    clientSecret: ENV.spotifyClientSecret,
  });

  const refreshToken = await TokenService.getSpotifyToken(area.userId);

  if (refreshToken == null) return null;
  spotifyApi.setRefreshToken(refreshToken);

  const accessToken = (await spotifyApi.refreshAccessToken()).body;

  spotifyApi.setAccessToken(accessToken.access_token);

  const currentSong = (await spotifyApi.getMyCurrentPlayingTrack()).body.item
    ?.id;

  if (currentSong == undefined) return null;
  const isLiked = await spotifyApi.containsMySavedTracks([currentSong]);
  console.log(isLiked);
  return null;
  //const likedTrack = await spotifyApi.containsMySavedTracks();
};

export { checkMusicSkip, checkIsMusicLiked };
