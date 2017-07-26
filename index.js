"use strict";
var builder = require("botbuilder");
var botbuilder_azure = require("botbuilder-azure");
var path = require('path');

var useEmulator = (process.env.NODE_ENV == 'development');

var connector = useEmulator ? new builder.ChatConnector() : new botbuilder_azure.BotServiceConnector({
    appId: process.env['MicrosoftAppId'],
    appPassword: process.env['MicrosoftAppPassword'],
    stateEndpoint: process.env['BotStateEndpoint'],
    openIdMetadata: process.env['BotOpenIdMetadata']
    
});

var bot = new builder.UniversalBot(connector);
bot.localePath(path.join(__dirname, './locale'));

//var recognizer = new builder.LuisRecognizer("https://westcentralus.api.cognitive.microsoft.com/luis/v2.0/apps/38fa8e1b-4495-4187-accc-9301800ffd18?subscription-key=a6d991277dd340939ea81a3da5503f57&timezoneOffset=0&verbose=true&q="); 
//bot.recognizer(recognizer); 


bot.dialog('/', function (session ){ //, args, next) {
         //var airportEntity = builder.EntityRecognizer.findEntity(args.intent.entities, '定时提醒'); 
           //if (airportEntity) { 
             // airport entity detected, continue to next step 
           //  session.dialogData.searchType = 'airport'; 
         //    next({ response: airportEntity.entity }); 
       //  } else {
             // no entities detected, ask user for a destination 
     //        builder.Prompts.text(session, 'Please enter  what you want to do'); 
   //      } 

    session.send('You said ' + session.message.text);
});
//.triggerAction({ 
//     matches: '定时提醒', 
//     onInterrupted: function (session) { 
//        session.send('Please provide a destination'); 
//     } 
// }); 


if (useEmulator) {
    var restify = require('restify');
    var server = restify.createServer();
    server.listen(3978, function() {
        console.log('test bot endpont at http://localhost:3978/api/messages');
    });
    server.post('/api/messages', connector.listen());    
} else {
    module.exports = { default: connector.listen() }
}
