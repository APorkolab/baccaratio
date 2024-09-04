const Dotenv = require('dotenv-webpack');

module.exports = {
  plugins: [
    new Dotenv({
      path: './.env', // Az env fájl helye
      systemvars: true, // Ha hozzáférést szeretnél a rendszer környezeti változókhoz
    }),
  ],
};
