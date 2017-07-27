// This loads the environment variables from the .env file
require('dotenv-extended').load();

var axios = require('axios');
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
    var hotelEntity = builder.EntityRecognizer.findEntity(args.intent.entities, 'datetimeV2');
      session.endDialog('小威已经给你预定提醒！时间为：' + JSON.stringify(args.intent.entities )+hotelEntity);
}).triggerAction({
    matches: 'remind'
});
 
bot.dialog('None', function (session, args) {
    // retrieve hotel name from matched entities
    //var hotelEntity = builder.EntityRecognizer.findEntity(args.intent.entities, 'Hotel');

     retrieveKB(session) ;
      // session.endDialog('干嘛！无聊！');
}).triggerAction({
    matches: 'None'
});

bot.dialog('help', function (session) {
 
   retrieveKB(session) ;
   

    //ession.endDialog('我暂时只提供定时提醒服务！');
}).triggerAction({
    matches: 'help'
});


  axios.defaults.baseURL = 'https://westus.api.cognitive.microsoft.com/qnamaker/v2.0/knowledgebases/f1f1bb67-75a4-4829-b0ad-53416969e978/generateAnswer';
  axios.defaults.headers.common['Ocp-Apim-Subscription-Key'] = '970f8ee823244168acf07ea3938afcd9';
  axios.defaults.headers.post['Content-Type'] = 'application/json';
 
 function retrieveKB(session){
 
    axios.post( axios.defaults.baseURL ,{"question": session.message.text})
    .then(function (response) {
    console.log(JSON.stringify(response.data));
     session.endDialog(response.data.answers[0].answer) ;
    })
    .catch(function (error) {
    console.log(error);
    });
     
 }


 
