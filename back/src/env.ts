import * as dotenv from "dotenv";

dotenv.config();

const ENV = {
  port: (process.env.PORT as string) || "3000",
  secret: process.env.SECRET as string,
  clientPort: (process.env.CLIENT_PORT as string) || "8081",
  googleApiKey: process.env.YOUTUBE_API_TOKEN as string,
  host: (process.env.HOST as string) || "0.0.0.0",
  discordBotToken: process.env.DISCORD_BOT_TOKEN as string,
};

export default ENV;
