/* eslint-disable @typescript-eslint/no-var-requires */
const webpack = require("webpack");
const fs = require("fs");
const path = require("path");

const envPath = path.resolve(__dirname, ".env");

// Töltsd be a .env-et, ha létezik; ha nem, marad üres objektum.
let injectedEnv = {};
if (fs.existsSync(envPath)) {
  require("dotenv").config({ path: envPath });
  injectedEnv = process.env;
}

// ngx-build-plus: funkciót exportálunk, hogy a meglévő confighoz hozzáadjuk a plugint
module.exports = (config) => {
  config.plugins = config.plugins || [];
  config.plugins.push(
    new webpack.DefinePlugin({
      // csak azt injektáljuk, ami a .env-ben (vagy a folyamatban) van; üresen is működik
      "process.env": JSON.stringify(injectedEnv),
    }),
  );
  return config;
};
