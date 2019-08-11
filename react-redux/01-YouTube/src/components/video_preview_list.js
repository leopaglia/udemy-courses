import React from 'react';
import VideoPreview from './video_preview';

const VideoPreviewList = (props) => {

	const videoPreviews = props.videos.map(video => {
		return (
			<VideoPreview
				onVideoSelect={props.onVideoSelect}
				key={video.etag}
				video={video}/>
		);
	});

	return(
		<ul className="list-group col-md-4">
			{videoPreviews}
		</ul>
	)
};

export default VideoPreviewList;
