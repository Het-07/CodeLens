const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");
const HtmlPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");

module.exports = {
    mode: "development",
    devtool: "cheap-module-source-map",
    entry: {
        popup: path.resolve("src/popup/index.tsx"),
        background: path.resolve("src/service-worker/background.ts"),
        content: path.resolve("src/contentScript/content.ts"),
        register: path.resolve("src/views/Register/Register.tsx")
    },
    module: {
        rules: [
            {
                use: "ts-loader",
                test: /\.(tsx|ts)$/,
                exclude: /node_modules/,
            },
            {
                test: /\.css$/i,
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.(woff|woff2|eot|ttf|otf|svg|png|jpg|gif)$/,
                type: "asset/resource",
            },
        ],
    },
    plugins: [
        new CleanWebpackPlugin({
            cleanStaleWebpackAssets: false,
        }),
        new CopyPlugin({
            patterns: [
                {
                    from: path.resolve("src/static"),
                    to: path.resolve("dist"),
                },
            ],
        }),
        ...getHtmlPlugins(["popup"]),
    ],
    resolve: {
        extensions: [".tsx", ".ts", ".js"],
    },
    output: {
        filename: "[name].js",
        path: path.resolve("dist"),
    },
    optimization: {
        splitChunks: {
            chunks: 'all'
        },
    },
};

function getHtmlPlugins(chunks) {
    return chunks.map(
        (chunk) =>
            new HtmlPlugin({
                title: "Codelens Extension with ReactJS",
                filename: `${chunk}.html`,
                chunks: [chunk],
            })
    );
}
