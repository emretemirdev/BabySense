
const functions = require("firebase-functions");

exports.helloWorld=functions.https.onCall((data,context) =>{
    return{message: "MERHABA CLOUD FUNCTİON"};
});