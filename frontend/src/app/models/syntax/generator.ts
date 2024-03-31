import {TreeNode} from "primeng/api";
import {InputData} from "./utils";

export interface ResultFile {path: string, type?: string, content: string[]}

/** Convert result files to tree nodes */
export function resultFilesToTree(files: ResultFile[]): TreeNode[] {
  const nodes: TreeNode[] = [];
  for(const file of files) {
    let current = nodes;
    const path: string[] = file.path.split('/');
    for(let i = 0; i < path.length; ++i) {
      if(i === path.length-1) {
        current.push({key: path[i], label: path[i], data: file.content.join('')});
      } else {
        let folder = current.find(f => f.key === path[i]);
        if(!folder) {
          folder = {key: path[i], label: path[i], children: []};
          current.push(folder);
        }
        current = folder.children!;
      }
    }
  }
  return nodes;
}

export interface Generator {
  /** Returns true if the file generation process is still ongoing. */
  get running(): boolean;

  /** Stops the file generation process. */
  stop(): void;

  /** Returns parser documentation */
  get docGenerator(): string

  /**
   * Parses and loads the source code of the generator.
   * @param text Source code of the generator
   * @throws ParsingError ParsingError
   * @returns Generated generator config
   */
  parseSrcGenerator(text: string): any;

  /**
   * Generates text files using given syntax and input data.
   * @param inputData Input data
   * @param config generator config
   * @throws GeneratorError GeneratorError
   * @returns The generated files
   */
  generate(inputData: InputData, config: any): ResultFile[];
}
