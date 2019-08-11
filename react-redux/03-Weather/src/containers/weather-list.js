import React, { Component } from 'react';
import { connect } from 'react-redux';
import WeatherChart from '../components/weather-chart';
import GoogleMap from '../components/google-map';

class WeatherList extends Component {
	renderWeather({city, list}) {
		const temps = list.map(el => el.main.temp);
		const pressures = list.map(el => el.main.pressure);
		const humidities = list.map(el => el.main.humidity);
		const { lat, lon } = city.coord;

		return (
			<tr key={city.id}>
				<td><GoogleMap lat={lat} lon={lon}/></td>
				<td><WeatherChart data={temps} color="red" units="°C"/></td>
				<td><WeatherChart data={pressures} color="blue" units="hPa"/></td>
				<td><WeatherChart data={humidities} color="black" units="%"/></td>
			</tr>
		)
	}

	render() {
		return (
			<table className="table table-hover">
				<thead>
					<tr>
						<th>City</th>
						<th>Temperature</th>
						<th>Pressure</th>
						<th>Humidity</th>
					</tr>
				</thead>
				<tbody>
					{this.props.weather.map(this.renderWeather)}
				</tbody>
			</table>
		)
	}
}

function mapStateToProps({ weather }) {
	return { weather }
}

export default connect(mapStateToProps)(WeatherList);
