import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import YTSearch from 'youtube-api-search';
import _ from 'lodash';

import SearchBar from './components/search_bar';
import VideoPreviewList from './components/video_preview_list';
import VideoDetail from './components/video_detail';

const API_KEY = 'AIzaSyB4lk5WncaKDL-SBZUSGh7j47d_JFyRYVQ';

class App extends Component {
	constructor(props) {
		super(props);

		this.state = {
			videos: [],
			selectedVideo: null
		};

		this.searchVideo('League of Legends');
	}

	searchVideo(term) {
		YTSearch(
			{key: API_KEY, term: term}, videos => this.setState({
				videos: videos,
				selectedVideo: videos[0]
			})
		);
	}

	render() {
		const searchVideo = _.debounce((term) => {this.searchVideo(term)}, 300);

		return (
			<div>
				<SearchBar onSearch={searchVideo}/>
				<VideoDetail video={this.state.selectedVideo}/>
				<VideoPreviewList
					onVideoSelect={selectedVideo => this.setState({selectedVideo})}
					videos={this.state.videos}/>
			</div>
		)
	}
}

ReactDOM.render(<App/>, document.querySelector('.container'));
