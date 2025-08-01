const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 9789,
    proxy: {
      '/api': {
        // target: 'http://8.130.78.1:9789',
        target: 'http://127.0.0.1:8989',
        changeOrigin: true
      }
    },
    client: {
      overlay: false
    }
  }
})
