import { renderComponent, expect } from '../test_helper';
import CommentList from '../../src/components/comment_list';

describe('CommentList component', () => {

	let component;

	beforeEach(() => {
		const state = { comments: ['comment 1', 'comment 2'] };
		component = renderComponent(CommentList, null, state);
	});

	it('has classname corresponding to component name', () => {
		expect(component).to.have.class('comment-list');
	});

	it('shows a LI for each comment', () => {
		expect(component.find('li').length).to.equal(2);
	});

	it('shows every provided comment', () => {
		expect(component).to.contain('comment 1');
		expect(component).to.contain('comment 2');
	});
});
