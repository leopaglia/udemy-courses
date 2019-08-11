import { expect } from '../test_helper';
import commentsReducer from '../../src/reducers/comments';
import { SAVE_COMMENT } from '../../src/actions/types';

describe('Comments reducer', () => {

	it('handles action with unknown type', () => {
		const state = commentsReducer([], 'RANDOM_ACTION_123');
		expect(state).to.deep.equal([]);
	});

	it('handles SAVE_COMMENT action with empty state', () => {
		const comment = 'a comment';
		const action = { type: SAVE_COMMENT, payload: comment };
		const state = commentsReducer([], action);
		expect(state).to.deep.equal(['a comment']);
	});

	it('handles SAVE_COMMENT action with filled state', () => {
		const comment = 'a comment';
		const action = { type: SAVE_COMMENT, payload: comment };
		const state = commentsReducer(['old comment'], action);
		expect(state).to.deep.equal(['old comment', 'a comment']);
	});

});
