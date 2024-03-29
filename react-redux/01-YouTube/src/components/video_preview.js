import React from 'react';

const VideoPreview = ({video, onVideoSelect}) => {

	const imageUrl = video.snippet.thumbnails.default.url;
	const title = video.snippet.title;

	return(
		<li className="list-group-item" onClick={() => onVideoSelect(video)}>
			<div className="video-list media">

				<div className="media-left">
					<img src={imageUrl} className="media-object"/>
				</div>

				<div className="media-body">
					<div className="media-heading">
						{title}
					</div>
				</div>

			</div>
		</li>
	)
};

export default VideoPreview;
