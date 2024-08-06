import JSZip from 'jszip';

/**
 * 压缩文件处理
 * @param {Blob|ArrayBuffer} zipFileStream - 压缩文件流，类型可以是 Blob 或 ArrayBuffer
 * @param {boolean} [directSave=false] - 是否直接保存文件
 * @returns {Promise<Array<{fileName: string, length: number, relativePath: string, blob: Blob}>>} - 返回一个 Promise，该 Promise 解析为一个包含文件信息对象的数组
 */
export function zipHandler(zipFileStream, directSave = false) {
	return JSZip.loadAsync(zipFileStream).then((zip) => {
		console.log('Zip file loaded:', zip);
		
		const fileContentPromises = [];
		zip.forEach((relativePath, zipEntry) => {
			const filePromise = zipEntry.async('blob').then((content) => {
				const file = {
					fileName: zipEntry.name,
					length: content.size,
					blob: content,
					relativePath: relativePath,
				};
				
				if (directSave) {
					saveFile(zipEntry.name, file.blob);
				}
				
				return file;
			});
		});
		
		return Promise.all(fileContentPromises);
	}).catch((err) => {
		console.error('Error processing zip file:', err)
		throw err;
	});
}

/**
 * 压缩文件处理
 * @param {Blob|ArrayBuffer} zipFileStream
 * @returns {Promise}
 */
export function saveFile(fileName, blob) {
    return new Promise((resolve, reject) => {
		console.log(fileName)
        const trimmedFileName = fileName.split('/').pop();
        const fileReader = new FileReader();

        fileReader.onload = (event) => {
			const arrayBuffer = event.target.result;

			// 获取文件系统路径
			const fs = uni.getFileSystemManager();
			const savePath = `${uni.env.USER_DATA_PATH}/${trimmedFileName}`;

			// 写入文件
			fs.writeFile({
				filePath: savePath,
				data: arrayBuffer,
				encoding: 'binary',
				success: () => {
					resolve();
				},
				fail: (error) => {
					console.error('保存文件失败:', error);
					reject(error);
				}
			});
        };

        fileReader.readAsArrayBuffer(blob);
    });
}