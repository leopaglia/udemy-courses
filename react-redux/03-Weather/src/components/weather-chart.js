import React, { Component } from 'react';
import { Sparklines, SparklinesLine, SparklinesReferenceLine } from 'react-sparklines';

function avg(data) {
	return parseFloat(data.reduce((a, b) => a + b) / data.length).toFixed(1);
}

export default (props) => {
	return (
		<div>
			<Sparklines data={props.data} height={120} width={180}>
				<SparklinesLine color={props.color}/>
				<SparklinesReferenceLine type="avg"/>
			</Sparklines>
			<div>{avg(props.data)} {props.units}</div>
		</div>
	)
}
