module.exports = {
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  // // css相关配置
  // css: {
  //   // 是否使用css分离插件 ExtractTextPlugin
  //   extract: true,
  //   // 开启 CSS source maps?
  //   sourceMap: false,
  //   // css预设器配置项
  //   loaderOptions: {
  //     // sass: {
  //     //   prependData: '@import "~@/assets/css/common.scss";'
  //     // },
  //   },
  //   // 启用 CSS requireModuleExtension
  //   requireModuleExtension: true
  // },
  chainWebpack: config => {
    config.entry.app = ['babel-polyfill', './src/main.js'];
  },
  productionSourceMap: process.env.NODE_ENV !== 'production',
}
