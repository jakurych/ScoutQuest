const {initializeApp} = require("firebase-admin/app");

initializeApp();

const {checkOpenQuestionFunctionV2} = require("./checkOpenQuestion");
const {checkPhotoFunction} = require("./checkPhotoTask");

exports.checkOpenQuestionFunctionV2 = checkOpenQuestionFunctionV2;
exports.checkPhotoFunction = checkPhotoFunction;
