const cacheName = 'pwa';

const filesToCacheOnInstall = [
	'/bootstrap.js',
	'/material.min.css',
	'/material.min.js',
	'/static/js/bundle.js',
	'/index.html',
	'/'
];

self.addEventListener('install', event => {
	event.waitUntil((
		caches.open(cacheName).then(cache => {
			cache.addAll(filesToCacheOnInstall);
		})
	));
});

self.addEventListener('fetch', event => {
	event.respondWith(
		caches.match(event.request).then(response => {
			return response || fetch(event.request);
		})
	);
});