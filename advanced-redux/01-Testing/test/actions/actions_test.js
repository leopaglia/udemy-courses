import { expect } from '../test_helper';
import { saveComment } from '../../src/actions';
import { SAVE_COMMENT } from '../../src/actions/types';

describe('Actions', () => {

	describe('saveComment', () => {

		it('has the correct type', () => {
			const action = saveComment();
			expect(action.type).to.equal(SAVE_COMMENT);
		});

		it('has the correct payload', () => {
			const comment = 'many tests wow';
			const action = saveComment(comment);
			expect(action.payload).to.equal(comment);
		});

	});

});
