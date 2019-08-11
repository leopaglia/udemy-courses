import axios from 'axios';

const API_KEY = 'e9b13e2e4d6727b92e8ce580d9ad816e';
const API_URL = `http://api.openweathermap.org/data/2.5/forecast?appid=${API_KEY}&units=metric`;
const COUNTRY_CODE = 'us';

export const FETCH_WEATHER = 'FETCH_WEATHER';

export function fetchWeather(city) {
	const url = `${API_URL}&q=${city},${COUNTRY_CODE}`;
	const request = axios.get(url);

	return {
		type: FETCH_WEATHER,
		payload: request
	}
}
