// This loads the environment variables from the .env file
require('dotenv-extended').load();

var builder = require('botbuilder');
var restify = require('restify');

// Setup Restify Server
var server = restify.createServer();
server.listen(process.env.port || process.env.PORT || 80, function () {
    console.log('%s listening to %s', server.name, server.url);
});
// Create connector and listen for messages
var connector = new builder.ChatConnector({
    appId: process.env.MICROSOFT_APP_ID,
    appPassword: process.env.MICROSOFT_APP_PASSWORD
});
server.post('/api/messages', connector.listen());

var bot = new builder.UniversalBot(connector, function (session) {
    session.send('Sorry, I did not understand \'%s\'. Type \'help\' if you need assistance.', session.message.text);
});

// You can provide your own model by specifing the 'LUIS_MODEL_URL' environment variable
// This Url can be obtained by uploading or creating your model from the LUIS portal: https://www.luis.ai/
var recognizer = new builder.LuisRecognizer(process.env.LUIS_MODEL_URL);
bot.recognizer(recognizer);

bot.dialog('remind', function (session, args) {
    // retrieve hotel name from matched entities
    var hotelEntity = builder.EntityRecognizer.findEntity(args.intent.entities, 'datetime');
      session.endDialog('小威已经给你预定提醒！');
}).triggerAction({
    matches: 'remind'
});
 
bot.dialog('None', function (session, args) {
    // retrieve hotel name from matched entities
    //var hotelEntity = builder.EntityRecognizer.findEntity(args.intent.entities, 'Hotel');
      session.endDialog('干嘛！无聊！');
}).triggerAction({
    matches: 'None'
});

bot.dialog('help', function (session) {
    session.endDialog('我暂时只提供定时提醒服务！');
}).triggerAction({
    matches: 'help'
});
 