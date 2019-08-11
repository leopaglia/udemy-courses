const express = require('express');
const expressGraphQL = require('express-graphql');
const schema = require('./schema');

const PORT = 4000;

const graphQLConfig = {
  graphiql: true,
  schema
};

const app = express();

app.use('/graphql', expressGraphQL(graphQLConfig));

app.listen(PORT, () => {
  console.log(`listening on port ${PORT}`);
});
