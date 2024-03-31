import {TreeNode} from "primeng/api";
import JSZip from "jszip";

/** Save blob to local disk with the given filename. */
export function saveBlob(filename: string, blob: Blob) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.target = '_blank';
  link.download = filename;
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

/** Zip tree nodes and save the result to local disk. */
export function zipNodes(nodes: TreeNode[]) {
  const zip = new JSZip();
  _zipNodes(nodes, zip);
  zip.generateAsync({type: 'blob'})
    .then(blob => saveBlob('result.zip', blob));
}

function _zipNodes(nodes: TreeNode[], folder: JSZip) {
  for(const node of nodes) {
    if(node.label) {
      if(node.children) _zipNodes(node.children, folder.folder(node.label)!);
      else if(node.data) folder.file(node.label, node.data);
    }
  }
}
