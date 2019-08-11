import { renderComponent, expect } from '../test_helper';
import CommentBox from '../../src/components/comment_box';

describe('CommentBox component', () => {

	let component;

	beforeEach(() => {
		component = renderComponent(CommentBox);
	});

	it('has classname corresponding to component name', () => {
		expect(component).to.have.class('comment-box');
	});

	it('has a textarea', () => {
		expect(component.find('textarea')).to.exist;
	});

	it('has a button', () => {
		expect(component.find('button')).to.exist;
	});

	describe('when entering text', () => {

		beforeEach(() => {
			component.find('textarea').simulate('change', 'some text');
		});

		it('shows it in the textarea', () => {
			expect(component.find('textarea')).to.have.value('some text');
		});

		describe('on submit', () => {

			beforeEach(() => {
				component.simulate('submit');
			});

			it('clears the input', () => {
				expect(component.find('textarea')).to.have.value('');
			});

			//TODO: TEST IF CALLS THE ACTION CREATOR
		});

	});
});
