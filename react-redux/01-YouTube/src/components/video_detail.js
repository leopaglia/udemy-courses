import React from 'react';

const VideoDetail = ({video}) => {

	if(!video) {
		return <div className="col-md-8">Loading...</div>;
	}

	const videoId = video.id.videoId;
	const url = `https://www.youtube.com/embed/${videoId}`;

	return(
		<div className="video-detail col-md-8">

			<div className="embed-responsive embed-responsive-16by9">
				<iframe src={url} className="embed-responsive-item"/>
			</div>

			<div className="details">
				<div className="title">{video.snippet.title}</div>
				<div className="description">{video.snippet.description}</div>
			</div>

		</div>
	)
};

export default VideoDetail;
