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

bot.dialog('Business_Remind', function (session, args) {
    // retrieve hotel name from matched entities
    var datetimeV2 = builder.EntityRecognizer.findEntity(args.intent.entities, 'datetimeV2');
    var timeStr = args.intent.entities[0].resolution.values.length >1? args.intent.entities[0].resolution.values[1].value :
    args.intent.entities[0].entity;
      session.endDialog('小薇已经给你预定提醒！时间为：' + timeStr);
}).triggerAction({
    matches: 'Business_Remind'
});

bot.dialog('Business_Remind_Times', function (session, args) {
    // retrieve hotel name from matched entities
    var datetimeV2 = builder.EntityRecognizer.findEntity(args.intent.entities, 'datetimeV2');
      var timeStr = args.intent.entities[0].resolution.values.length >1? args.intent.entities[0].resolution.values[1].value :
    args.intent.entities[0].entity;
      session.endDialog('小薇已经给你预定提醒！时间为：' + timeStr);
}).triggerAction({
    matches: 'Business_Remind_Times'
});
 

bot.dialog('FAQ_Help', function (session) {
   retrieveKB(session,'你会啥') ;
}).triggerAction({
    matches: 'FAQ_Help'
});

bot.dialog('FAQ_Hi', function (session) {
   retrieveKB(session,'hi') ;
}).triggerAction({
    matches: 'FAQ_Hi'
});

bot.dialog('FAQ_Introduce', function (session) {
   retrieveKB(session,'你是谁') ;
}).triggerAction({
    matches: 'FAQ_Introduce'
});

bot.dialog('FAQ_Leave', function (session) {
   session.endDialog('代请假功能即将开放！');
}).triggerAction({
    matches: 'FAQ_Leave'
});

bot.dialog('None', function (session, args) {

     retrieveKB(session) ;
      // session.endDialog('干嘛！无聊！');
}).triggerAction({
    matches: 'None'
});





  axios.defaults.baseURL = 'https://westus.api.cognitive.microsoft.com/qnamaker/v2.0/knowledgebases/f1f1bb67-75a4-4829-b0ad-53416969e978/generateAnswer';
  axios.defaults.headers.common['Ocp-Apim-Subscription-Key'] = '970f8ee823244168acf07ea3938afcd9';
  axios.defaults.headers.post['Content-Type'] = 'application/json';
 
 function retrieveKB(session,message){
 
    axios.post( axios.defaults.baseURL ,{"question": message||session.message.text})
    .then(function (response) {
    console.log(JSON.stringify(response.data));
     session.endDialog(response.data.answers[0].answer) ;
    })
    .catch(function (error) {
    console.log(error);
    });
     
 }


 
