import JSZip from 'jszip';

/**
 * 文件处理回调方法
 * @callback FileCallback
 * @param {FileInfo} fileInfo - 文件信息对象
 * @returns {void}
 */

/**
 * @typedef {Object} FileInfo
 * @property {string} content - 文件内容 base64 格式字符串
 * @property {number} size - 文件大小
 * @property {string} name - 文件名称
 */

/**
 * 压缩文件处理
 * @param {Blob|ArrayBuffer} zipFileStream - 压缩文件流，类型可以是 Blob 或 ArrayBuffer
 * @param {FileCallback} unZipFileCallback - 解析出来每一个文件的内容
 * @param {function} callback - 一个回调方法，接受一个文件信息对象作为参数
 * @param {Object} unZipFileCallback.fileInfo - 文件信息对象
 * @param {string} unZipFileCallback.fileInfo.content - 文件内容
 * @param {number} unZipFileCallback.fileInfo.size - 文件大小
 * @param {string} unZipFileCallback.fileInfo.type - 文件类型
 */
export function zipHandler(zipFileStream, unZipFileCallback) {
	JSZip.loadAsync(zipFileStream).then((zip) => {
		console.log('Zip file loaded');
		
		const fileContentPromises = [];
		zip.forEach((relativePath, zipEntry) => {
			const filePromise = zipEntry.async('base64').then((content) => {
				const file = {
					name: zipEntry.name,
					content: content,
					relativePath: relativePath,
					size: zipEntry._data.uncompressedSize
				}
				
				console.log(file.name, file.size)
				
				// saveFileToLocal(zipEntry.name, content);
				if (unZipFileCallback) {
					unZipFileCallback(file)
				}
			}).catch((err) => {
				console.error('Error processing zip file:', zipEntry.name, err);
			});
		});
	}).catch((err) => {
		console.error('Error processing zip file:', err)
	});
}

/**
 * @param {string} filename
 * @param {stirng} content
 */
function saveFileToLocal(filename, content) {
	// #ifdef APP-PLUS
	plus.io.requestFileSystem(plus.io.RelativeURL, (fs) => {
	    fs.root.getFile(filename, { create: true }, (fileEntry) => {
			fileEntry.createWriter((writer) => {
				writer.onwriteend = () => {
					console.log('File saved successfully:', filename);
					// this.readFileFromLocal(filename);
				};
				writer.onerror = (e) => {
					console.error('Failed to save file:', e);
				};
				writer.writeAsBinary(content);
			}, (e) => {
				console.error('Failed to create writer:', e);
			});
		}, (e) => {
			console.error('Failed to get file:', e);
		});
	}, (e) => {
	    console.error('Failed to request file system:', e);
	});
	// #endif
}