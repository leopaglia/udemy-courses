const API_URL = 'http://reduxblog.herokuapp.com/api';
const API_KEY = 'react-redux-course';

export function url(path, ...params) {
	const url = `${API_URL}/${path}?key=${API_KEY}`;
	if(params.length) {
		return url.concat(`&${params.join('&')}`);
	}
	return url;
}
