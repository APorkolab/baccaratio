const webpack = require('webpack');
const Dotenv = require('dotenv-webpack');

module.exports = {
  plugins: [
    new Dotenv({
      path: './.env', // .env fájl elérési útja
      systemvars: true,
    }),
    new webpack.DefinePlugin({
      API_BASE_URL: JSON.stringify(process.env.API_BASE_URL || 'https://api.baccaratio.porkolab.hu')
    })
  ]
};