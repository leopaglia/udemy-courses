const axios = require('axios');
const graphql = require('graphql');

const API_URL = 'http://localhost:3000';
const USERS_API_URL = `${API_URL}/users`;
const COMPANIES_API_URL = `${API_URL}/companies`;

const {
  GraphQLList,
  GraphQLInt,
  GraphQLNonNull,
  GraphQLObjectType,
  GraphQLSchema,
  GraphQLString
} = graphql;

const CompanyType = new GraphQLObjectType({
  name: 'Company',
  fields: () => ({
    id: { type: GraphQLString },
    name: { type: GraphQLString },
    description: { type: GraphQLString },
    users: {
      type: GraphQLList(UserType),
      resolve: parentValue => axios.get(`${COMPANIES_API_URL}/${parentValue.id}/users`).then(r => r.data)
    }
  })
});

const UserType = new GraphQLObjectType({
  name: 'User',
  fields: () => ({
    id: { type: GraphQLString },
    firstName: { type: GraphQLString },
    age: { type: GraphQLInt },
    company: {
      type: CompanyType,
      resolve: parentValue => axios.get(`${COMPANIES_API_URL}/${parentValue.companyId}`).then(r => r.data)
    }
  })
});

const query = new GraphQLObjectType({
  name: 'RootQueryType',
  fields: {
    company: {
      type: CompanyType,
      args: { id: { type: GraphQLString } },
      resolve: (_, args) => axios.get(`${COMPANIES_API_URL}/${args.id}`).then(r => r.data)
    },
    user: {
      type: UserType,
      args: { id: { type: GraphQLString } },
      resolve: (_, args) => axios.get(`${USERS_API_URL}/${args.id}`).then(r => r.data)
    }
  }
});

const mutation = new GraphQLObjectType({
  name: 'Mutation',
  fields: {
    addUser: {
      type: UserType,
      args: {
        firstName: { type: new GraphQLNonNull(GraphQLString) },
        age: { type: new GraphQLNonNull(GraphQLInt) },
        companyId: { type: GraphQLString }
      },
      resolve: (_, { firstName, age, companyId }) => axios.post(USERS_API_URL, {
        firstName,
        age,
        companyId
      }).then(r => r.data)
    },
    deleteUser: {
      type: UserType,
      args: {
        id: { type: new GraphQLNonNull(GraphQLString) }
      },
      resolve: (_, { id }) => axios.delete(`${USERS_API_URL}/${id}`).then(r => r.data)
    },
    updateUser: {
      type: UserType,
      args: {
        id: { type: new GraphQLNonNull(GraphQLString) },
        firstName: { type: GraphQLString },
        age: { type: GraphQLInt },
        companyId: { type: GraphQLString }
      },
      resolve: (_, { id, firstName, age, companyId }) => axios.patch(`${USERS_API_URL}/${id}`, {
        firstName,
        age,
        companyId
      }).then(r => r.data)
    },
  }
});

module.exports = new GraphQLSchema({ mutation, query });