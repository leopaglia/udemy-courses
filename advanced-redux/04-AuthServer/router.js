const Authentication = require('./controllers/authentication');
const passportService = require('./services/passport');
const passport = require('passport');

const requireSignin = passport.authenticate('local', { session: false });
const requireAuth = passport.authenticate('jwt', { session: false });

module.exports = function(app) {
    app.get('/', requireAuth, function(req, res) {
        setTimeout(function(){ res.send({ message: 'Super secret code is: 8008135' }); }, 3000);
        // res.send({ message: 'Super secret code is: 8008135' })
    });
    app.post('/signin', requireSignin, Authentication.signin);
    app.post('/signup', Authentication.signup);
};

