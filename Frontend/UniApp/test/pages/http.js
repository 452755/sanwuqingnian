/**
 * 
 * @param {post请求方式} options 
 */
export const PostRequest = (options) => {
	// uni.showModal({
	// 	title: '...',
	// 	icon: 'success'
	// })

	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://192.168.31.45:19808/TzxRestFulServer/' + options.url,
			method: 'POST',
			data: options.data || {},
			header: {
				"content-type": "application/json;charset=UTF-8",
			},
			success: (res) => {
				resolve(res)
			},
			fail: (err) => {
				uni.showModal({
					title: JSON.stringify(err),
					icon: 'error'
				})
				// alert(JSON.stringify(err))
				console.log(err)
				reject(err)
			}
		})
	})
}


/**
 * 
 * @param {get请求方式} options 
 */
export const GetRequest = (options) => {

	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://192.168.31.45:19808/TzxRestFulServer/' + options.url,
			method: 'GET',
			data: options.data || {},
			header: {
				"content-type": "application/json;charset=UTF-8"
			},
			success: (res) => {
				resolve(res)
			},
			fail: (err) => {
				console.log(err)
				reject(err)
			}
		})
	})
}

export const DownLoadFile = (options) => {
	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://192.168.31.45:19808/TzxRestFulServer/' + options.url,
			method: options.method,
			data: options.data || {},
			header: {
				"content-type": "application/json;charset=UTF-8"
			},
			responseType: 'arraybuffer', 
			success: (res) => {
				resolve(res)
			},
			fail: (err) => {
				console.log(err)
				reject(err)
			}
		})
	})
}